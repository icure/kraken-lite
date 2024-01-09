package org.taktik.icure.asynclogic.objectstorage

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.reactive.asPublisher
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.InitializingBean
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.core.io.buffer.DefaultDataBufferFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.codec.json.Jackson2JsonDecoder
import org.springframework.http.codec.json.Jackson2JsonEncoder
import org.springframework.stereotype.Service
import org.springframework.util.DigestUtils
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientRequestException
import org.springframework.web.reactive.function.client.awaitBodilessEntity
import org.springframework.web.reactive.function.client.awaitBody
import org.springframework.web.reactive.function.client.bodyToFlow
import org.taktik.icure.asynclogic.utils.CloudAuthenticationLogic
import org.taktik.icure.entities.Document
import org.taktik.icure.entities.base.HasDataAttachments
import org.taktik.icure.properties.ObjectStorageProperties
import org.taktik.icure.utils.toByteArray
import reactor.core.publisher.Mono
import java.io.Serializable
import java.net.URI
import java.net.URISyntaxException
import java.util.*
import java.util.concurrent.ConcurrentHashMap

interface ObjectStorageClientBean<T : HasDataAttachments<T>> : ObjectStorageClient<T>, InitializingBean {}

@OptIn(ExperimentalCoroutinesApi::class)
private class ObjectStorageClientImpl<T : HasDataAttachments<T>>(
	private val objectStorageProperties: ObjectStorageProperties,
	private val cloudAuthenticationLogic: CloudAuthenticationLogic,
	override val entityGroupName: String
) : ObjectStorageClientBean<T> {
	companion object {
		private const val NEXT_BYTE_HEADER = "Next-Byte"
		private val log = LoggerFactory.getLogger(ObjectStorageClientImpl::class.java)
		private val icureCloudClient = WebClient.builder()
			.codecs { clientDefaultCodecsConfigurer ->
				val mapper = ObjectMapper().registerKotlinModule()
				clientDefaultCodecsConfigurer.defaultCodecs().jackson2JsonEncoder(Jackson2JsonEncoder(mapper, MediaType.APPLICATION_JSON))
				clientDefaultCodecsConfigurer.defaultCodecs().jackson2JsonDecoder(Jackson2JsonDecoder(mapper, MediaType.APPLICATION_JSON))
			}
			.build()
	}
	private val nextBytesMap = ConcurrentHashMap<Pair<String, String>, Int>()

	override fun afterPropertiesSet() {
		val icureCloudUrl = objectStorageProperties.icureCloudUrl
		try {
			require(icureCloudUrl.isNotBlank()) { "icureCloudUrl can't be blank" }
			URI(icureCloudUrl)
		} catch (e: URISyntaxException) {
			throw IllegalArgumentException("icureCloudUrl=\"$icureCloudUrl\" must be a valid uri", e)
		}
	}

	override suspend fun upload(entity: T, attachmentId: String, content: Flow<DataBuffer>, userId: String): Boolean =
		unsafeUpload(entity.id, attachmentId, content, userId)

	override suspend fun unsafeUpload(
		entityId: String,
		attachmentId: String,
		content: Flow<DataBuffer>,
		userId: String
	): Boolean = runCatching {
		val contentBytes = content.toByteArray(true)
		val md5 = Base64.getUrlEncoder().encodeToString(DigestUtils.md5Digest(contentBytes))
		val nextExpectedByte = nextBytesMap[entityId to attachmentId] ?: 0
		val nextBytes = if (nextExpectedByte > 0) contentBytes.sliceArray(nextExpectedByte until contentBytes.size) else contentBytes
		icureCloudClient.post()
			.uri("${uriTo(entityId, attachmentId)}?size=${contentBytes.size}&md5Hash=$md5&startByte=$nextExpectedByte")
			.setAuthorization(userId)
			.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE)
			.body(BodyInserters.fromDataBuffers(flowOf(DefaultDataBufferFactory.sharedInstance.wrap(nextBytes)).asPublisher()))
			.retrieve()
			.awaitBodilessEntity().let { response ->
				when (response.statusCode) {
					HttpStatus.OK -> true
					HttpStatus.MOVED_PERMANENTLY -> false.also {
						response.headers[NEXT_BYTE_HEADER]?.firstOrNull()?.toIntOrNull()?.let {
							nextBytesMap[entityId to attachmentId] = it
						}
					}
					else -> false.also { log.warn("Unexpected upload response: $response") }
				}
			}
	}.onFailure {
		log.warn("Error while uploading attachment $attachmentId@$entityId:$entityGroupName", it)
	}.getOrNull() == true

	override fun get(entity: T, attachmentId: String, userId: String): Flow<DataBuffer> = flow {
		try {
			emitAll(
				icureCloudClient.get()
					.uri(uriTo(entity.id, attachmentId))
					.setAuthorization(userId)
					.retrieve()
					.onStatus({ it == HttpStatus.NOT_FOUND }) {
						Mono.just(UnavailableObjectException("There is no attachment $attachmentId@${entity.id}:$entityGroupName", null))
					}
					.bodyToFlow()
			)
		} catch (e: WebClientRequestException) {
			throw UnreachableObjectStorageException(e)
		}
	}

	override suspend fun checkAvailable(entity: T, attachmentId: String, userId: String): Boolean =
		runCatching {
			val info = icureCloudClient.get()
				.uri("${uriTo(entity.id, attachmentId)}/info")
				.setAuthorization(userId)
				.retrieve()
				.awaitBody<StoredObjectInformation>()
			when (info) {
				is StoredObjectInformation.Available -> true
				is StoredObjectInformation.Storing -> false.also {
					nextBytesMap[entity.id to attachmentId] = info.nextByte.toInt()
				}
				else -> false
			}
		}.onFailure {
			log.warn("Error while checking availability of attachment $attachmentId@${entity.id}:${entityGroupName}", it)
		}.getOrNull() == true

	// Deletion is actually handled by maintenance tasks
	override suspend fun delete(entity: T, attachmentId: String, userId: String): Boolean =
		true

	// Deletion is actually handled by maintenance tasks
	override suspend fun unsafeDelete(entityId: String, attachmentId: String, userId: String): Boolean =
		true

	private fun uriTo(entityId: String, attachmentId: String): String = try {
		URI(objectStorageProperties.icureCloudUrl).toString()
	} catch (_: URISyntaxException) {
		throw IllegalStateException("Malformed icure cloud uri ${objectStorageProperties.icureCloudUrl}")
	}.let { cloudUrl ->
		"$cloudUrl/${attachmentRoute(entityId, attachmentId)}"
	}

	private suspend fun <S : WebClient.RequestHeadersSpec<S>> WebClient.RequestHeadersSpec<S>.setAuthorization(userId: String): S =
		this.header("Authorization", cloudAuthenticationLogic.userAuthenticationHeader(userId))

	private fun attachmentRoute(entityId: String, attachmentId: String): String =
		listOf("rest", "v1", "objectstorage", entityGroupName, entityId, attachmentId).joinToString("/")

	/**
	 * Represents the status of an object which may be stored in the object storage service.
	 * Note: for proper serialization with Jackson the kotlin module must be installed.
	 */
	@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
	@JsonSubTypes(
		JsonSubTypes.Type(value = StoredObjectInformation.Available::class, name = "Available"),
		JsonSubTypes.Type(value = StoredObjectInformation.Storing::class, name = "Storing"),
		JsonSubTypes.Type(value = StoredObjectInformation.NotStored::class, name = "NotStored"),
	)
	private sealed class StoredObjectInformation {
		/**
		 * The object is fully stored and available.
		 * @param md5HashHexString hex string representation of the md5 hash of the content.
		 */
		data class Available(val md5HashHexString: String) : StoredObjectInformation(), Serializable

		/**
		 * The object is currently getting stored.
		 * @param nextByte the next expected byte of the object content (all bytes up until the previous have already been stored).
		 * @param md5HashHexString md5 hash of the expected hash of the full content as an hex string.
		 */
		data class Storing(val nextByte: Long, val md5HashHexString: String?) : StoredObjectInformation(), Serializable

		/**
		 * The object is not stored in the object storage service and it is not getting stored.
		 */
		object NotStored : StoredObjectInformation(), Serializable { override fun toString() = "NotStored" }
	}
}

class DocumentObjectStorageClientImpl(
	objectStorageProperties: ObjectStorageProperties,
	cloudAuthenticationLogic: CloudAuthenticationLogic
) : DocumentObjectStorageClient, ObjectStorageClientBean<Document> by ObjectStorageClientImpl(
	objectStorageProperties,
	cloudAuthenticationLogic,
	"documents"
)
