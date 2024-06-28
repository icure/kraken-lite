package org.taktik.icure.asyncservice.impl

import kotlinx.coroutines.flow.Flow
import org.springframework.stereotype.Service
import org.taktik.couchdb.ViewQueryResultEvent
import org.taktik.couchdb.entity.IdAndRev
import org.taktik.icure.asynclogic.CodeLogic
import org.taktik.icure.asyncservice.CodeService
import org.taktik.icure.db.PaginationOffset
import org.taktik.icure.domain.filter.chain.FilterChain
import org.taktik.icure.entities.base.Code
import org.taktik.icure.entities.base.CodeStub
import org.taktik.icure.pagination.PaginationElement
import java.io.InputStream

@Service
class CodeServiceImpl(
    private val codeLogic: CodeLogic
) : CodeService {
    override fun getTagTypeCandidates(): List<String> = codeLogic.getTagTypeCandidates()

    override fun getRegions(): List<String> = codeLogic.getRegions()

    override suspend fun get(id: String): Code? = codeLogic.get(id)

    override suspend fun get(type: String, code: String, version: String): Code? = codeLogic.get(type, code, version)

    override fun getCodes(ids: List<String>): Flow<Code> = codeLogic.getCodes(ids)

    override suspend fun create(code: Code): Code? = codeLogic.create(code)

    override suspend fun create(batch: List<Code>): List<Code>? = codeLogic.create(batch)

    override suspend fun modify(code: Code): Code? = codeLogic.modify(code)

    override fun modify(batch: List<Code>): Flow<Code> = codeLogic.modify(batch)

    override fun listCodeTypesBy(region: String?, type: String?): Flow<String> = codeLogic.listCodeTypesBy(region, type)

    override fun findCodesBy(region: String?, type: String?, code: String?, version: String?): Flow<Code> = codeLogic.findCodesBy(region, type, code, version)

    override fun findCodesBy(
        region: String?,
        type: String?,
        code: String?,
        version: String?,
        paginationOffset: PaginationOffset<List<String?>>
    ): Flow<PaginationElement> = codeLogic.findCodesBy(region, type, code, version, paginationOffset)

    override fun findCodesByLabel(
        region: String?,
        language: String,
        types: Set<String>,
        label: String,
        version: String?,
        paginationOffset: PaginationOffset<List<String?>>
    ): Flow<PaginationElement> = codeLogic.findCodesByLabel(region, language, types, label, version, paginationOffset)

    override fun listCodeIdsByTypeCodeVersionInterval(
        startType: String?,
        startCode: String?,
        startVersion: String?,
        endType: String?,
        endCode: String?,
        endVersion: String?
    ): Flow<String> = codeLogic.listCodeIdsByTypeCodeVersionInterval(startType, startCode, startVersion, endType, endCode, endVersion)

    override fun findCodesByQualifiedLinkId(
        region: String?,
        linkType: String,
        linkedId: String?,
        pagination: PaginationOffset<List<String>>
    ): Flow<PaginationElement> = codeLogic.findCodesByQualifiedLinkId(region, linkType, linkedId, pagination)

    override fun listCodeIdsByQualifiedLinkId(linkType: String, linkedId: String?): Flow<String> = codeLogic.listCodeIdsByQualifiedLinkId(linkType, linkedId)

    override suspend fun <T : Enum<*>> importCodesFromEnum(e: Class<T>) = codeLogic.importCodesFromEnum(e)

    override suspend fun importCodesFromXml(md5: String, type: String, stream: InputStream) = codeLogic.importCodesFromXml(md5, type, stream)

    override suspend fun importCodesFromJSON(stream: InputStream) = codeLogic.importCodesFromJSON(stream)

    override fun listCodes(
        paginationOffset: PaginationOffset<*>?,
        filterChain: FilterChain<Code>,
        sort: String?,
        desc: Boolean?
    ): Flow<ViewQueryResultEvent> = codeLogic.listCodes(paginationOffset, filterChain, sort, desc)

    override suspend fun getOrCreateCode(type: String, code: String, version: String): Code? = codeLogic.getOrCreateCode(type, code, version)

    override suspend fun isValid(type: String, code: String, version: String?): Boolean = codeLogic.isValid(type, code, version)

    override suspend fun isValid(code: Code, ofType: String?): Boolean = codeLogic.isValid(code, ofType)

    override suspend fun isValid(code: CodeStub, ofType: String?): Boolean = codeLogic.isValid(code, ofType)

    override suspend fun getCodeByLabel(region: String?, label: String, type: String, languages: List<String>): Code? = codeLogic.getCodeByLabel(region, label, type, languages)

    override fun solveConflicts(limit: Int?, ids: List<String>?) = codeLogic.solveConflicts(limit, ids)
}
