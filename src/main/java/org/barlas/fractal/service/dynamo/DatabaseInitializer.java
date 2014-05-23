package org.barlas.fractal.service.dynamo;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.GlobalSecondaryIndex;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.Projection;
import com.amazonaws.services.dynamodbv2.model.ProjectionType;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.ScalarAttributeType;
import com.amazonaws.services.dynamodbv2.model.TableDescription;
import com.amazonaws.services.dynamodbv2.util.Tables;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;

public class DatabaseInitializer {

    private static final Logger logger = Logger.getLogger(DatabaseInitializer.class);

    private static final String indexSuffix = "-index";
    private static final String userId = "userId";
    private static final String email = "email";
    private static final String tagName = "tagName";
    private static final String scriptId = "scriptId";

    @Value("${scriptsTableName}")
    private String scriptsTableName;
    @Value("${tagsTableName}")
    private String tagsTableName;
    @Value("${usersTableName}")
    private String usersTableName;
    @Autowired
    private AmazonDynamoDBClient dynamo;

    @PostConstruct
    public void init() {
        createUsersTable();
        createTagsTable();
        createScriptsTable();
    }

    private void createTagsTable() {
        if (Tables.doesTableExist(dynamo, tagsTableName)) {
            logger.info("Table " + tagsTableName + " is already ACTIVE");
            return;
        }

        CreateTableRequest createTableRequest = new CreateTableRequest()
                .withTableName(tagsTableName)
                .withKeySchema(
                        new KeySchemaElement()
                                .withAttributeName(scriptId)
                                .withKeyType(KeyType.HASH),
                        new KeySchemaElement()
                                .withAttributeName(tagName)
                                .withKeyType(KeyType.RANGE))
                .withGlobalSecondaryIndexes(
                        new GlobalSecondaryIndex()
                                .withIndexName(tagName + indexSuffix)
                                .withKeySchema(new KeySchemaElement().withKeyType(KeyType.HASH).withAttributeName(tagName))
                                .withProjection(new Projection().withProjectionType(ProjectionType.ALL))
                                .withProvisionedThroughput(new ProvisionedThroughput().withReadCapacityUnits(1L).withWriteCapacityUnits(1L))
                )
                .withGlobalSecondaryIndexes(
                        new GlobalSecondaryIndex()
                                .withIndexName(userId + indexSuffix)
                                .withKeySchema(new KeySchemaElement().withKeyType(KeyType.HASH).withAttributeName(userId))
                                .withProjection(new Projection().withProjectionType(ProjectionType.ALL))
                                .withProvisionedThroughput(new ProvisionedThroughput().withReadCapacityUnits(1L).withWriteCapacityUnits(1L))
                )
                .withAttributeDefinitions(
                        new AttributeDefinition()
                                .withAttributeName(scriptId)
                                .withAttributeType(ScalarAttributeType.S),
                        new AttributeDefinition()
                                .withAttributeName(tagName)
                                .withAttributeType(ScalarAttributeType.S),
                        new AttributeDefinition()
                                .withAttributeName(userId)
                                .withAttributeType(ScalarAttributeType.S))
                .withProvisionedThroughput(
                        new ProvisionedThroughput()
                                .withReadCapacityUnits(1L)
                                .withWriteCapacityUnits(1L));

        TableDescription createdTableDescription = dynamo.createTable(createTableRequest).getTableDescription();
        logger.info("Created Table: " + createdTableDescription);

        // Wait for it to become active
        logger.info("Waiting for " + tagsTableName + " to become ACTIVE...");
        Tables.waitForTableToBecomeActive(dynamo, tagsTableName);
    }

    private void createUsersTable() {
        if (Tables.doesTableExist(dynamo, usersTableName)) {
            logger.info("Table " + usersTableName + " is already ACTIVE");
            return;
        }

        CreateTableRequest createTableRequest = new CreateTableRequest()
                .withTableName(usersTableName)
                .withKeySchema(
                        new KeySchemaElement()
                                .withAttributeName(userId)
                                .withKeyType(KeyType.HASH))
                .withGlobalSecondaryIndexes(
                        new GlobalSecondaryIndex()
                                .withIndexName(email + indexSuffix)
                                .withKeySchema(new KeySchemaElement().withKeyType(KeyType.HASH).withAttributeName(email))
                                .withProjection(new Projection().withProjectionType(ProjectionType.ALL))
                                .withProvisionedThroughput(new ProvisionedThroughput().withReadCapacityUnits(1L).withWriteCapacityUnits(1L))
                )
                .withAttributeDefinitions(
                        new AttributeDefinition()
                                .withAttributeName(userId)
                                .withAttributeType(ScalarAttributeType.S),
                        new AttributeDefinition()
                                .withAttributeName(email)
                                .withAttributeType(ScalarAttributeType.S)
                )
                .withProvisionedThroughput(
                        new ProvisionedThroughput()
                                .withReadCapacityUnits(1L)
                                .withWriteCapacityUnits(1L)
                );

        TableDescription createdTableDescription = dynamo.createTable(createTableRequest).getTableDescription();
        logger.info("Created Table: " + createdTableDescription);

        // Wait for it to become active
        logger.info("Waiting for " + usersTableName + " to become ACTIVE...");
        Tables.waitForTableToBecomeActive(dynamo, usersTableName);
    }

    private void createScriptsTable() {
        if (Tables.doesTableExist(dynamo, scriptsTableName)) {
            logger.info("Table " + scriptsTableName + " is already ACTIVE");
            return;
        }

        CreateTableRequest createTableRequest = new CreateTableRequest()
                .withTableName(scriptsTableName)
                .withKeySchema(
                        new KeySchemaElement()
                                .withAttributeName(scriptId)
                                .withKeyType(KeyType.HASH))
                .withGlobalSecondaryIndexes(
                        new GlobalSecondaryIndex()
                                .withIndexName(userId + indexSuffix)
                                .withKeySchema(new KeySchemaElement().withKeyType(KeyType.HASH).withAttributeName(userId))
                                .withProjection(new Projection().withProjectionType(ProjectionType.ALL))
                                .withProvisionedThroughput(new ProvisionedThroughput().withReadCapacityUnits(1L).withWriteCapacityUnits(1L))
                )
                .withAttributeDefinitions(
                        new AttributeDefinition()
                                .withAttributeName(scriptId)
                                .withAttributeType(ScalarAttributeType.S),
                        new AttributeDefinition()
                                .withAttributeName(userId)
                                .withAttributeType(ScalarAttributeType.S)
                )
                .withProvisionedThroughput(
                        new ProvisionedThroughput()
                                .withReadCapacityUnits(1L)
                                .withWriteCapacityUnits(1L)
                );

        TableDescription createdTableDescription = dynamo.createTable(createTableRequest).getTableDescription();
        logger.info("Created Table: " + createdTableDescription);

        // Wait for it to become active
        logger.info("Waiting for " + scriptsTableName + " to become ACTIVE...");
        Tables.waitForTableToBecomeActive(dynamo, scriptsTableName);
    }

}
