package org.thingsboard.rule.engine.action;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.util.DateTime;
import lombok.extern.slf4j.Slf4j;
import org.thingsboard.rule.engine.api.*;
import org.thingsboard.rule.engine.api.util.TbNodeUtils;
import org.thingsboard.server.common.data.downtime_entry.DowntimeEntry;
import org.thingsboard.server.common.data.id.DeviceId;
import org.thingsboard.server.common.data.plugin.ComponentType;
import org.thingsboard.server.common.msg.TbMsg;
import org.thingsboard.server.dao.downtimeentry.DowntimeEntryService;

import java.io.IOException;
import java.util.Date;
import java.util.Iterator;

import static org.thingsboard.rule.engine.api.TbRelationTypes.SUCCESS;

@Slf4j
@RuleNode(
        type = ComponentType.ACTION,
        name = "create downtime entry",
        relationTypes = {"True", "False"},
        configClazz = DowntimeEntryNodeConfiguration.class,
        nodeDescription = "Checks for state other than 1001 and creates a downtime entry in the database.",
        nodeDetails = "If state <> 1001 - send Message via <b>True</b> chain, otherwise <b>False</b> chain is used.",
        uiResources = {"static/rulenode/custom-nodes-config.js"},
        configDirective = "tbFilterNodeCheckKeyConfig")
public class RubikxCreateDowntimeEntryNode implements TbNode {
    private static final ObjectMapper mapper = new ObjectMapper();

    private DowntimeEntryNodeConfiguration config;
    private String inputKey;
    private Integer stateCode;

    @Override
    public void init(TbContext tbContext, TbNodeConfiguration configuration) throws TbNodeException {
        this.config = TbNodeUtils.convert(configuration, DowntimeEntryNodeConfiguration.class);
        this.inputKey = config.getInputKey();
        this.stateCode = config.getStateCode();
    }

    @Override
    public void onMsg(TbContext ctx, TbMsg msg) {
        try {
            JsonNode jsonNode = mapper.readTree(msg.getData());
            Iterator<String> iterator = jsonNode.fieldNames();
            while (iterator.hasNext()) {
                String field = iterator.next();
                if (field.startsWith(inputKey) && jsonNode.get(field).asInt() != stateCode) {
                    DowntimeEntryService downtimeEntryService = ctx.getDowntimeEntryService();
                    DowntimeEntry downtimeEntry = new DowntimeEntry();
                    downtimeEntry.setDeviceId(new DeviceId(msg.getOriginator().getId()));
                    downtimeEntry.setName("Downtime");
                    downtimeEntry.setTenantId(ctx.getTenantId());
                    downtimeEntry.setCreatedTime(new DateTime(new Date()).getValue());
                    downtimeEntry.setStartDateTimeMs(new DateTime(new Date()).getValue());
                    DowntimeEntry savedDowntimeEntry = downtimeEntryService.saveDowntimeEntry(downtimeEntry);
                    System.out.println("created downtime entry with id: " + savedDowntimeEntry.getId());
                }
            }
            ctx.tellNext(msg, SUCCESS);

        } catch (IOException e) {
            ctx.tellFailure(msg, e);
        }
    }

    @Override
    public void destroy() {

    }
}
