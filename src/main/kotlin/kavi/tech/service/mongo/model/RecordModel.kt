package kavi.tech.service.mongo.model

import io.vertx.rxjava.ext.mongo.MongoClient
import kavi.tech.service.common.extension.logger
import kavi.tech.service.mongo.component.AbstractModel
import kavi.tech.service.mongo.schema.Record
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository


@Repository
class RecordModel @Autowired constructor(val client: MongoClient) : AbstractModel<Record>(client, Record.tableName, Record::class.java){

    override val log = logger(this::class)

    private val tableName = Record.tableName

}