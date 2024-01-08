/*
 * Copyright (c) 2020. Taktik SA, All rights reserved.
 */

package org.taktik.icure.asyncservice

import java.nio.ByteBuffer
import kotlinx.coroutines.flow.Flow
import org.springframework.security.access.AccessDeniedException
import org.taktik.couchdb.DocIdentifier
import org.taktik.icure.asyncservice.base.EntityWithSecureDelegationsService
import org.taktik.icure.entities.Receipt
import org.taktik.icure.entities.embed.ReceiptBlobType
import org.taktik.icure.exceptions.NotFoundRequestException

interface ReceiptService : EntityWithSecureDelegationsService<Receipt> {
	suspend fun createReceipt(receipt: Receipt): Receipt?
	suspend fun modifyReceipt(receipt: Receipt): Receipt
	fun listReceiptsByReference(ref: String): Flow<Receipt>
	fun getAttachment(receiptId: String, attachmentId: String): Flow<ByteBuffer>

	suspend fun addReceiptAttachment(receipt: Receipt, blobType: ReceiptBlobType, payload: ByteArray): Receipt

	/**
	 * Creates a batch of [Receipt]s.
	 *
	 * @param receipts a [Collection] of [Receipt]s to create.
	 * @return a [Flow] containing the created [Receipt]s.
	 * @throws [AccessDeniedException] if the user does not have the permissions to create [Receipt]s.
	 */
	fun createReceipts(receipts: Collection<Receipt>): Flow<Receipt>

	/**
	 * Deletes [Receipt]s in batch. If the current user does not have the permissions to delete one or more [Receipt]s
	 * in the batch, then those [Receipt]s will be ignored and no error will be thrown.
	 *
	 * @param identifiers a [Collection] of ids of the [Receipt]s to delete.
	 * @return a [Flow] containing the [DocIdentifier]s of the successfully deleted [Receipt]s.
	 * @throws [AccessDeniedException] if the user does not meet the precondition to delete [Receipt]s.
	 */
	fun deleteReceipts(identifiers: Collection<String>): Flow<DocIdentifier>

	/**
	 * Deletes a [Receipt].
	 *
	 * @param receiptId the id of the [Receipt] to delete.
	 * @return a [DocIdentifier] related to the [Receipt] if the operation completes successfully.
	 * @throws [AccessDeniedException] if the current user does not have the permission to delete the [Receipt].
	 * @throws [NotFoundRequestException] if an [Receipt] with the specified [receiptId] does not exist.
	 */
	suspend fun deleteReceipt(receiptId: String): DocIdentifier

	/**
	 * Retrieve a [Receipt] by id.
	 *
	 * @param id the id of the [Receipt] to retrieve
	 * @return the [Receipt] or null, if it does not exist.
	 * @throws [AccessDeniedException] if the user does not meet the preconditions to access [Receipt]s or if
	 * it does not have the permissions to access that [Receipt]
	 */
	suspend fun getReceipt(id: String): Receipt?
}
