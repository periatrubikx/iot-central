/**
 * Copyright © 2016-2021 The Thingsboard Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.thingsboard.server.service.install;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.thingsboard.common.util.ThingsBoardThreadFactory;
import org.thingsboard.server.common.data.AdminSettings;
import org.thingsboard.server.common.data.Customer;
import org.thingsboard.server.common.data.DataConstants;
import org.thingsboard.server.common.data.Device;
import org.thingsboard.server.common.data.DeviceProfile;
import org.thingsboard.server.common.data.DeviceProfileProvisionType;
import org.thingsboard.server.common.data.DeviceProfileType;
import org.thingsboard.server.common.data.DeviceTransportType;
import org.thingsboard.server.common.data.Tenant;
import org.thingsboard.server.common.data.TenantProfile;
import org.thingsboard.server.common.data.User;
import org.thingsboard.server.common.data.alarm.AlarmSeverity;
import org.thingsboard.server.common.data.device.profile.AlarmCondition;
import org.thingsboard.server.common.data.device.profile.AlarmConditionFilter;
import org.thingsboard.server.common.data.device.profile.AlarmConditionFilterKey;
import org.thingsboard.server.common.data.device.profile.AlarmConditionKeyType;
import org.thingsboard.server.common.data.device.profile.AlarmRule;
import org.thingsboard.server.common.data.device.profile.DefaultDeviceProfileConfiguration;
import org.thingsboard.server.common.data.device.profile.DefaultDeviceProfileTransportConfiguration;
import org.thingsboard.server.common.data.device.profile.DeviceProfileAlarm;
import org.thingsboard.server.common.data.device.profile.DeviceProfileData;
import org.thingsboard.server.common.data.device.profile.DisabledDeviceProfileProvisionConfiguration;
import org.thingsboard.server.common.data.device.profile.SimpleAlarmConditionSpec;
import org.thingsboard.server.common.data.id.CustomerId;
import org.thingsboard.server.common.data.id.DeviceId;
import org.thingsboard.server.common.data.id.DeviceProfileId;
import org.thingsboard.server.common.data.id.TenantId;
import org.thingsboard.server.common.data.kv.BaseAttributeKvEntry;
import org.thingsboard.server.common.data.kv.BasicTsKvEntry;
import org.thingsboard.server.common.data.kv.BooleanDataEntry;
import org.thingsboard.server.common.data.kv.DoubleDataEntry;
import org.thingsboard.server.common.data.kv.LongDataEntry;
import org.thingsboard.server.common.data.page.PageLink;
import org.thingsboard.server.common.data.query.BooleanFilterPredicate;
import org.thingsboard.server.common.data.query.DynamicValue;
import org.thingsboard.server.common.data.query.DynamicValueSourceType;
import org.thingsboard.server.common.data.query.EntityKeyValueType;
import org.thingsboard.server.common.data.query.FilterPredicateValue;
import org.thingsboard.server.common.data.query.NumericFilterPredicate;
import org.thingsboard.server.common.data.rule.RuleChainType;
import org.thingsboard.server.common.data.security.Authority;
import org.thingsboard.server.common.data.security.DeviceCredentials;
import org.thingsboard.server.common.data.security.UserCredentials;
import org.thingsboard.server.common.data.tenant.profile.DefaultTenantProfileConfiguration;
import org.thingsboard.server.common.data.tenant.profile.TenantProfileData;
import org.thingsboard.server.common.data.widget.WidgetsBundle;
import org.thingsboard.server.dao.attributes.AttributesService;
import org.thingsboard.server.dao.customer.CustomerService;
import org.thingsboard.server.dao.device.DeviceCredentialsService;
import org.thingsboard.server.dao.device.DeviceProfileService;
import org.thingsboard.server.dao.device.DeviceService;
import org.thingsboard.server.dao.exception.DataValidationException;
import org.thingsboard.server.dao.rule.RuleChainService;
import org.thingsboard.server.dao.settings.AdminSettingsService;
import org.thingsboard.server.dao.tenant.TenantProfileService;
import org.thingsboard.server.dao.tenant.TenantService;
import org.thingsboard.server.dao.timeseries.TimeseriesService;
import org.thingsboard.server.dao.user.UserService;
import org.thingsboard.server.dao.widget.WidgetsBundleService;

import javax.annotation.Nullable;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@Profile("install")
@Slf4j
public class DefaultSystemDataLoaderService implements SystemDataLoaderService {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    public static final String CUSTOMER_CRED = "customer";
    public static final String DEFAULT_DEVICE_TYPE = "default";
    public static final String ACTIVITY_STATE = "active";

    @Autowired
    private InstallScripts installScripts;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private UserService userService;

    @Autowired
    private AdminSettingsService adminSettingsService;

    @Autowired
    private WidgetsBundleService widgetsBundleService;

    @Autowired
    private TenantService tenantService;

    @Autowired
    private TenantProfileService tenantProfileService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private DeviceProfileService deviceProfileService;

    @Autowired
    private AttributesService attributesService;

    @Autowired
    private DeviceCredentialsService deviceCredentialsService;

    @Autowired
    private RuleChainService ruleChainService;

    @Autowired
    private TimeseriesService tsService;

    @Value("${state.persistToTelemetry:false}")
    @Getter
    private boolean persistActivityToTelemetry;

    @Bean
    protected BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    private ExecutorService tsCallBackExecutor;

    @PostConstruct
    public void initExecutor() {
        tsCallBackExecutor = Executors.newSingleThreadExecutor(ThingsBoardThreadFactory.forName("sys-loader-ts-callback"));
    }

    @PreDestroy
    public void shutdownExecutor() {
        if (tsCallBackExecutor != null) {
            tsCallBackExecutor.shutdownNow();
        }
    }

    @Override
    public void createSysAdmin() {
        createUser(Authority.SYS_ADMIN, null, null, "sysadmin@thingsboard.org", "sysadmin");
    }

    @Override
    public void createDefaultTenantProfiles() throws Exception {
        tenantProfileService.findOrCreateDefaultTenantProfile(TenantId.SYS_TENANT_ID);

        TenantProfileData tenantProfileData = new TenantProfileData();
        tenantProfileData.setConfiguration(new DefaultTenantProfileConfiguration());

        TenantProfile isolatedTbCoreProfile = new TenantProfile();
        isolatedTbCoreProfile.setDefault(false);
        isolatedTbCoreProfile.setName("Isolated TB Core");
        isolatedTbCoreProfile.setDescription("Isolated TB Core tenant profile");
        isolatedTbCoreProfile.setIsolatedTbCore(true);
        isolatedTbCoreProfile.setIsolatedTbRuleEngine(false);
        isolatedTbCoreProfile.setProfileData(tenantProfileData);
        try {
            tenantProfileService.saveTenantProfile(TenantId.SYS_TENANT_ID, isolatedTbCoreProfile);
        } catch (DataValidationException e) {
            log.warn(e.getMessage());
        }

        TenantProfile isolatedTbRuleEngineProfile = new TenantProfile();
        isolatedTbRuleEngineProfile.setDefault(false);
        isolatedTbRuleEngineProfile.setName("Isolated TB Rule Engine");
        isolatedTbRuleEngineProfile.setDescription("Isolated TB Rule Engine tenant profile");
        isolatedTbRuleEngineProfile.setIsolatedTbCore(false);
        isolatedTbRuleEngineProfile.setIsolatedTbRuleEngine(true);
        isolatedTbRuleEngineProfile.setProfileData(tenantProfileData);

        try {
            tenantProfileService.saveTenantProfile(TenantId.SYS_TENANT_ID, isolatedTbRuleEngineProfile);
        } catch (DataValidationException e) {
            log.warn(e.getMessage());
        }

        TenantProfile isolatedTbCoreAndTbRuleEngineProfile = new TenantProfile();
        isolatedTbCoreAndTbRuleEngineProfile.setDefault(false);
        isolatedTbCoreAndTbRuleEngineProfile.setName("Isolated TB Core and TB Rule Engine");
        isolatedTbCoreAndTbRuleEngineProfile.setDescription("Isolated TB Core and TB Rule Engine tenant profile");
        isolatedTbCoreAndTbRuleEngineProfile.setIsolatedTbCore(true);
        isolatedTbCoreAndTbRuleEngineProfile.setIsolatedTbRuleEngine(true);
        isolatedTbCoreAndTbRuleEngineProfile.setProfileData(tenantProfileData);

        try {
            tenantProfileService.saveTenantProfile(TenantId.SYS_TENANT_ID, isolatedTbCoreAndTbRuleEngineProfile);
        } catch (DataValidationException e) {
            log.warn(e.getMessage());
        }
    }

    @Override
    public void createAdminSettings() throws Exception {
        AdminSettings generalSettings = new AdminSettings();
        generalSettings.setKey("general");
        ObjectNode node = objectMapper.createObjectNode();
        node.put("baseUrl", "http://localhost:8080");
        node.put("prohibitDifferentUrl", false);
        generalSettings.setJsonValue(node);
        adminSettingsService.saveAdminSettings(TenantId.SYS_TENANT_ID, generalSettings);

        AdminSettings mailSettings = new AdminSettings();
        mailSettings.setKey("mail");
        node = objectMapper.createObjectNode();
        node.put("mailFrom", "RubikX IoT <sysadmin@localhost.localdomain>");
        node.put("smtpProtocol", "smtp");
        node.put("smtpHost", "localhost");
        node.put("smtpPort", "25");
        node.put("timeout", "10000");
        node.put("enableTls", false);
        node.put("username", "");
        node.put("password", "");
        node.put("tlsVersion", "TLSv1.2");//NOSONAR, key used to identify password field (not password value itself)
        node.put("enableProxy", false);
        node.put("showChangePassword", false);
        mailSettings.setJsonValue(node);
        adminSettingsService.saveAdminSettings(TenantId.SYS_TENANT_ID, mailSettings);
    }

    @Override
    public void createOAuth2Templates() throws Exception {
        installScripts.createOAuth2Templates();
    }

    @Override
    public void loadDemoData() throws Exception {
        Tenant demoTenant = new Tenant();
        demoTenant.setRegion("Global");
        demoTenant.setTitle("Tenant");
        demoTenant = tenantService.saveTenant(demoTenant);
        installScripts.loadDemoRuleChains(demoTenant.getId());
        createUser(Authority.TENANT_ADMIN, demoTenant.getId(), null, "tenant@thingsboard.org", "tenant");

        Customer customerA = new Customer();
        customerA.setTenantId(demoTenant.getId());
        customerA.setTitle("Customer A");
        customerA = customerService.saveCustomer(customerA);
        Customer customerB = new Customer();
        customerB.setTenantId(demoTenant.getId());
        customerB.setTitle("Customer B");
        customerB = customerService.saveCustomer(customerB);
        Customer customerC = new Customer();
        customerC.setTenantId(demoTenant.getId());
        customerC.setTitle("Customer C");
        customerC = customerService.saveCustomer(customerC);
        createUser(Authority.CUSTOMER_USER, demoTenant.getId(), customerA.getId(), "customer@rubikx.io", CUSTOMER_CRED);
        createUser(Authority.CUSTOMER_USER, demoTenant.getId(), customerA.getId(), "customerA@rubikx.io", CUSTOMER_CRED);
        createUser(Authority.CUSTOMER_USER, demoTenant.getId(), customerB.getId(), "customerB@rubikx.io", CUSTOMER_CRED);
        createUser(Authority.CUSTOMER_USER, demoTenant.getId(), customerC.getId(), "customerC@rubikx.io", CUSTOMER_CRED);

        DeviceProfile defaultDeviceProfile = this.deviceProfileService.findOrCreateDeviceProfile(demoTenant.getId(), DEFAULT_DEVICE_TYPE);

        createDevice(demoTenant.getId(), customerA.getId(), defaultDeviceProfile.getId(), "Test Device A1", "A1_TEST_TOKEN", null);
        createDevice(demoTenant.getId(), customerA.getId(), defaultDeviceProfile.getId(), "Test Device A2", "A2_TEST_TOKEN", null);
        createDevice(demoTenant.getId(), customerA.getId(), defaultDeviceProfile.getId(), "Test Device A3", "A3_TEST_TOKEN", null);
        createDevice(demoTenant.getId(), customerB.getId(), defaultDeviceProfile.getId(), "Test Device B1", "B1_TEST_TOKEN", null);
        createDevice(demoTenant.getId(), customerC.getId(), defaultDeviceProfile.getId(), "Test Device C1", "C1_TEST_TOKEN", null);

        createDevice(demoTenant.getId(), null, defaultDeviceProfile.getId(), "DHT11 Demo Device", "DHT11_DEMO_TOKEN", "Demo device that is used in sample " +
                "applications that upload data from DHT11 temperature and humidity sensor");

        createDevice(demoTenant.getId(), null, defaultDeviceProfile.getId(), "Raspberry Pi Demo Device", "RASPBERRY_PI_DEMO_TOKEN", "Demo device that is used in " +
                "Raspberry Pi GPIO control sample application");

        DeviceProfile thermostatDeviceProfile = new DeviceProfile();
        thermostatDeviceProfile.setTenantId(demoTenant.getId());
        thermostatDeviceProfile.setDefault(false);
        thermostatDeviceProfile.setName("thermostat");
        thermostatDeviceProfile.setType(DeviceProfileType.DEFAULT);
        thermostatDeviceProfile.setTransportType(DeviceTransportType.DEFAULT);
        thermostatDeviceProfile.setProvisionType(DeviceProfileProvisionType.DISABLED);
        thermostatDeviceProfile.setDescription("Thermostat device profile");
        thermostatDeviceProfile.setDefaultRuleChainId(ruleChainService.findTenantRuleChainsByType(
                demoTenant.getId(), RuleChainType.CORE, new PageLink(1, 0, "Thermostat")).getData().get(0).getId());

        DeviceProfileData deviceProfileData = new DeviceProfileData();
        DefaultDeviceProfileConfiguration configuration = new DefaultDeviceProfileConfiguration();
        DefaultDeviceProfileTransportConfiguration transportConfiguration = new DefaultDeviceProfileTransportConfiguration();
        DisabledDeviceProfileProvisionConfiguration provisionConfiguration = new DisabledDeviceProfileProvisionConfiguration(null);
        deviceProfileData.setConfiguration(configuration);
        deviceProfileData.setTransportConfiguration(transportConfiguration);
        deviceProfileData.setProvisionConfiguration(provisionConfiguration);
        thermostatDeviceProfile.setProfileData(deviceProfileData);

        DeviceProfileAlarm highTemperature = new DeviceProfileAlarm();
        highTemperature.setId("highTemperatureAlarmID");
        highTemperature.setAlarmType("High Temperature");
        AlarmRule temperatureRule = new AlarmRule();
        AlarmCondition temperatureCondition = new AlarmCondition();
        temperatureCondition.setSpec(new SimpleAlarmConditionSpec());

        AlarmConditionFilter temperatureAlarmFlagAttributeFilter = new AlarmConditionFilter();
        temperatureAlarmFlagAttributeFilter.setKey(new AlarmConditionFilterKey(AlarmConditionKeyType.ATTRIBUTE, "temperatureAlarmFlag"));
        temperatureAlarmFlagAttributeFilter.setValueType(EntityKeyValueType.BOOLEAN);
        BooleanFilterPredicate temperatureAlarmFlagAttributePredicate = new BooleanFilterPredicate();
        temperatureAlarmFlagAttributePredicate.setOperation(BooleanFilterPredicate.BooleanOperation.EQUAL);
        temperatureAlarmFlagAttributePredicate.setValue(new FilterPredicateValue<>(Boolean.TRUE));
        temperatureAlarmFlagAttributeFilter.setPredicate(temperatureAlarmFlagAttributePredicate);

        AlarmConditionFilter temperatureTimeseriesFilter = new AlarmConditionFilter();
        temperatureTimeseriesFilter.setKey(new AlarmConditionFilterKey(AlarmConditionKeyType.TIME_SERIES, "temperature"));
        temperatureTimeseriesFilter.setValueType(EntityKeyValueType.NUMERIC);
        NumericFilterPredicate temperatureTimeseriesFilterPredicate = new NumericFilterPredicate();
        temperatureTimeseriesFilterPredicate.setOperation(NumericFilterPredicate.NumericOperation.GREATER);
        FilterPredicateValue<Double> temperatureTimeseriesPredicateValue =
                new FilterPredicateValue<>(25.0, null,
                        new DynamicValue<>(DynamicValueSourceType.CURRENT_DEVICE, "temperatureAlarmThreshold"));
        temperatureTimeseriesFilterPredicate.setValue(temperatureTimeseriesPredicateValue);
        temperatureTimeseriesFilter.setPredicate(temperatureTimeseriesFilterPredicate);
        temperatureCondition.setCondition(Arrays.asList(temperatureAlarmFlagAttributeFilter, temperatureTimeseriesFilter));
        temperatureRule.setAlarmDetails("Current temperature = ${temperature}");
        temperatureRule.setCondition(temperatureCondition);
        highTemperature.setCreateRules(new TreeMap<>(Collections.singletonMap(AlarmSeverity.MAJOR, temperatureRule)));

        AlarmRule clearTemperatureRule = new AlarmRule();
        AlarmCondition clearTemperatureCondition = new AlarmCondition();
        clearTemperatureCondition.setSpec(new SimpleAlarmConditionSpec());

        AlarmConditionFilter clearTemperatureTimeseriesFilter = new AlarmConditionFilter();
        clearTemperatureTimeseriesFilter.setKey(new AlarmConditionFilterKey(AlarmConditionKeyType.TIME_SERIES, "temperature"));
        clearTemperatureTimeseriesFilter.setValueType(EntityKeyValueType.NUMERIC);
        NumericFilterPredicate clearTemperatureTimeseriesFilterPredicate = new NumericFilterPredicate();
        clearTemperatureTimeseriesFilterPredicate.setOperation(NumericFilterPredicate.NumericOperation.LESS_OR_EQUAL);
        FilterPredicateValue<Double> clearTemperatureTimeseriesPredicateValue =
                new FilterPredicateValue<>(25.0, null,
                        new DynamicValue<>(DynamicValueSourceType.CURRENT_DEVICE, "temperatureAlarmThreshold"));

        clearTemperatureTimeseriesFilterPredicate.setValue(clearTemperatureTimeseriesPredicateValue);
        clearTemperatureTimeseriesFilter.setPredicate(clearTemperatureTimeseriesFilterPredicate);
        clearTemperatureCondition.setCondition(Collections.singletonList(clearTemperatureTimeseriesFilter));
        clearTemperatureRule.setCondition(clearTemperatureCondition);
        clearTemperatureRule.setAlarmDetails("Current temperature = ${temperature}");
        highTemperature.setClearRule(clearTemperatureRule);

        DeviceProfileAlarm lowHumidity = new DeviceProfileAlarm();
        lowHumidity.setId("lowHumidityAlarmID");
        lowHumidity.setAlarmType("Low Humidity");
        AlarmRule humidityRule = new AlarmRule();
        AlarmCondition humidityCondition = new AlarmCondition();
        humidityCondition.setSpec(new SimpleAlarmConditionSpec());

        AlarmConditionFilter humidityAlarmFlagAttributeFilter = new AlarmConditionFilter();
        humidityAlarmFlagAttributeFilter.setKey(new AlarmConditionFilterKey(AlarmConditionKeyType.ATTRIBUTE, "humidityAlarmFlag"));
        humidityAlarmFlagAttributeFilter.setValueType(EntityKeyValueType.BOOLEAN);
        BooleanFilterPredicate humidityAlarmFlagAttributePredicate = new BooleanFilterPredicate();
        humidityAlarmFlagAttributePredicate.setOperation(BooleanFilterPredicate.BooleanOperation.EQUAL);
        humidityAlarmFlagAttributePredicate.setValue(new FilterPredicateValue<>(Boolean.TRUE));
        humidityAlarmFlagAttributeFilter.setPredicate(humidityAlarmFlagAttributePredicate);

        AlarmConditionFilter humidityTimeseriesFilter = new AlarmConditionFilter();
        humidityTimeseriesFilter.setKey(new AlarmConditionFilterKey(AlarmConditionKeyType.TIME_SERIES, "humidity"));
        humidityTimeseriesFilter.setValueType(EntityKeyValueType.NUMERIC);
        NumericFilterPredicate humidityTimeseriesFilterPredicate = new NumericFilterPredicate();
        humidityTimeseriesFilterPredicate.setOperation(NumericFilterPredicate.NumericOperation.LESS);
        FilterPredicateValue<Double> humidityTimeseriesPredicateValue =
                new FilterPredicateValue<>(60.0, null,
                        new DynamicValue<>(DynamicValueSourceType.CURRENT_DEVICE, "humidityAlarmThreshold"));
        humidityTimeseriesFilterPredicate.setValue(humidityTimeseriesPredicateValue);
        humidityTimeseriesFilter.setPredicate(humidityTimeseriesFilterPredicate);
        humidityCondition.setCondition(Arrays.asList(humidityAlarmFlagAttributeFilter, humidityTimeseriesFilter));

        humidityRule.setCondition(humidityCondition);
        humidityRule.setAlarmDetails("Current humidity = ${humidity}");
        lowHumidity.setCreateRules(new TreeMap<>(Collections.singletonMap(AlarmSeverity.MINOR, humidityRule)));

        AlarmRule clearHumidityRule = new AlarmRule();
        AlarmCondition clearHumidityCondition = new AlarmCondition();
        clearHumidityCondition.setSpec(new SimpleAlarmConditionSpec());

        AlarmConditionFilter clearHumidityTimeseriesFilter = new AlarmConditionFilter();
        clearHumidityTimeseriesFilter.setKey(new AlarmConditionFilterKey(AlarmConditionKeyType.TIME_SERIES, "humidity"));
        clearHumidityTimeseriesFilter.setValueType(EntityKeyValueType.NUMERIC);
        NumericFilterPredicate clearHumidityTimeseriesFilterPredicate = new NumericFilterPredicate();
        clearHumidityTimeseriesFilterPredicate.setOperation(NumericFilterPredicate.NumericOperation.GREATER_OR_EQUAL);
        FilterPredicateValue<Double> clearHumidityTimeseriesPredicateValue =
                new FilterPredicateValue<>(60.0, null,
                        new DynamicValue<>(DynamicValueSourceType.CURRENT_DEVICE, "humidityAlarmThreshold"));

        clearHumidityTimeseriesFilterPredicate.setValue(clearHumidityTimeseriesPredicateValue);
        clearHumidityTimeseriesFilter.setPredicate(clearHumidityTimeseriesFilterPredicate);
        clearHumidityCondition.setCondition(Collections.singletonList(clearHumidityTimeseriesFilter));
        clearHumidityRule.setCondition(clearHumidityCondition);
        clearHumidityRule.setAlarmDetails("Current humidity = ${humidity}");
        lowHumidity.setClearRule(clearHumidityRule);

        deviceProfileData.setAlarms(Arrays.asList(highTemperature, lowHumidity));

        DeviceProfile savedThermostatDeviceProfile = deviceProfileService.saveDeviceProfile(thermostatDeviceProfile);

        DeviceId t1Id = createDevice(demoTenant.getId(), null, savedThermostatDeviceProfile.getId(), "Thermostat T1", "T1_TEST_TOKEN", "Demo device for Thermostats dashboard").getId();
        DeviceId t2Id = createDevice(demoTenant.getId(), null, savedThermostatDeviceProfile.getId(), "Thermostat T2", "T2_TEST_TOKEN", "Demo device for Thermostats dashboard").getId();

        attributesService.save(demoTenant.getId(), t1Id, DataConstants.SERVER_SCOPE,
                Arrays.asList(new BaseAttributeKvEntry(System.currentTimeMillis(), new DoubleDataEntry("latitude", 37.3948)),
                        new BaseAttributeKvEntry(System.currentTimeMillis(), new DoubleDataEntry("longitude", -122.1503)),
                        new BaseAttributeKvEntry(System.currentTimeMillis(), new BooleanDataEntry("temperatureAlarmFlag", true)),
                        new BaseAttributeKvEntry(System.currentTimeMillis(), new BooleanDataEntry("humidityAlarmFlag", true)),
                        new BaseAttributeKvEntry(System.currentTimeMillis(), new LongDataEntry("temperatureAlarmThreshold", (long) 20)),
                        new BaseAttributeKvEntry(System.currentTimeMillis(), new LongDataEntry("humidityAlarmThreshold", (long) 50))));

        attributesService.save(demoTenant.getId(), t2Id, DataConstants.SERVER_SCOPE,
                Arrays.asList(new BaseAttributeKvEntry(System.currentTimeMillis(), new DoubleDataEntry("latitude", 37.493801)),
                        new BaseAttributeKvEntry(System.currentTimeMillis(), new DoubleDataEntry("longitude", -121.948769)),
                        new BaseAttributeKvEntry(System.currentTimeMillis(), new BooleanDataEntry("temperatureAlarmFlag", true)),
                        new BaseAttributeKvEntry(System.currentTimeMillis(), new BooleanDataEntry("humidityAlarmFlag", true)),
                        new BaseAttributeKvEntry(System.currentTimeMillis(), new LongDataEntry("temperatureAlarmThreshold", (long) 25)),
                        new BaseAttributeKvEntry(System.currentTimeMillis(), new LongDataEntry("humidityAlarmThreshold", (long) 30))));

        installScripts.loadDashboards(demoTenant.getId(), null);
    }

    @Override
    public void deleteSystemWidgetBundle(String bundleAlias) throws Exception {
        WidgetsBundle widgetsBundle = widgetsBundleService.findWidgetsBundleByTenantIdAndAlias(TenantId.SYS_TENANT_ID, bundleAlias);
        if (widgetsBundle != null) {
            widgetsBundleService.deleteWidgetsBundle(TenantId.SYS_TENANT_ID, widgetsBundle.getId());
        }
    }

    @Override
    public void loadSystemWidgets() throws Exception {
        installScripts.loadSystemWidgets();
    }

    @Override
    public void updateSystemWidgets() throws Exception {
        this.deleteSystemWidgetBundle("charts");
        this.deleteSystemWidgetBundle("cards");
        this.deleteSystemWidgetBundle("maps");
        this.deleteSystemWidgetBundle("analogue_gauges");
        this.deleteSystemWidgetBundle("digital_gauges");
        this.deleteSystemWidgetBundle("gpio_widgets");
        this.deleteSystemWidgetBundle("alarm_widgets");
        this.deleteSystemWidgetBundle("control_widgets");
        this.deleteSystemWidgetBundle("maps_v2");
        this.deleteSystemWidgetBundle("gateway_widgets");
        this.deleteSystemWidgetBundle("input_widgets");
        this.deleteSystemWidgetBundle("date");
        this.deleteSystemWidgetBundle("entity_admin_widgets");
        this.deleteSystemWidgetBundle("navigation_widgets");
        this.deleteSystemWidgetBundle("edge_widgets");
        installScripts.loadSystemWidgets();
    }

    private User createUser(Authority authority,
                            TenantId tenantId,
                            CustomerId customerId,
                            String email,
                            String password) {
        User user = new User();
        user.setAuthority(authority);
        user.setEmail(email);
        user.setTenantId(tenantId);
        user.setCustomerId(customerId);
        user = userService.saveUser(user);
        UserCredentials userCredentials = userService.findUserCredentialsByUserId(TenantId.SYS_TENANT_ID, user.getId());
        userCredentials.setPassword(passwordEncoder.encode(password));
        userCredentials.setEnabled(true);
        userCredentials.setActivateToken(null);
        userService.saveUserCredentials(TenantId.SYS_TENANT_ID, userCredentials);
        return user;
    }

    private Device createDevice(TenantId tenantId,
                                CustomerId customerId,
                                DeviceProfileId deviceProfileId,
                                String name,
                                String accessToken,
                                String description) {
        Device device = new Device();
        device.setTenantId(tenantId);
        device.setCustomerId(customerId);
        device.setDeviceProfileId(deviceProfileId);
        device.setName(name);
        if (description != null) {
            ObjectNode additionalInfo = objectMapper.createObjectNode();
            additionalInfo.put("description", description);
            device.setAdditionalInfo(additionalInfo);
        }
        device = deviceService.saveDevice(device);
        save(device.getId(), ACTIVITY_STATE, false);
        DeviceCredentials deviceCredentials = deviceCredentialsService.findDeviceCredentialsByDeviceId(TenantId.SYS_TENANT_ID, device.getId());
        deviceCredentials.setCredentialsId(accessToken);
        deviceCredentialsService.updateDeviceCredentials(TenantId.SYS_TENANT_ID, deviceCredentials);
        return device;
    }

    private void save(DeviceId deviceId, String key, boolean value) {
        if (persistActivityToTelemetry) {
            ListenableFuture<Integer> saveFuture = tsService.save(
                    TenantId.SYS_TENANT_ID,
                    deviceId,
                    Collections.singletonList(new BasicTsKvEntry(System.currentTimeMillis(), new BooleanDataEntry(key, value))), 0L);
            addTsCallback(saveFuture, new TelemetrySaveCallback<>(deviceId, key, value));
        } else {
            ListenableFuture<List<Void>> saveFuture = attributesService.save(TenantId.SYS_TENANT_ID, deviceId, DataConstants.SERVER_SCOPE,
                    Collections.singletonList(new BaseAttributeKvEntry(new BooleanDataEntry(key, value)
                    , System.currentTimeMillis())));
            addTsCallback(saveFuture, new TelemetrySaveCallback<>(deviceId, key, value));
        }
    }

    private static class TelemetrySaveCallback<T> implements FutureCallback<T> {
        private final DeviceId deviceId;
        private final String key;
        private final Object value;

        TelemetrySaveCallback(DeviceId deviceId, String key, Object value) {
            this.deviceId = deviceId;
            this.key = key;
            this.value = value;
        }

        @Override
        public void onSuccess(@Nullable T result) {
            log.trace("[{}] Successfully updated attribute [{}] with value [{}]", deviceId, key, value);
        }

        @Override
        public void onFailure(Throwable t) {
            log.warn("[{}] Failed to update attribute [{}] with value [{}]", deviceId, key, value, t);
        }
    }

    private <S> void addTsCallback(ListenableFuture<S> saveFuture, final FutureCallback<S> callback) {
        Futures.addCallback(saveFuture, new FutureCallback<S>() {
            @Override
            public void onSuccess(@Nullable S result) {
                callback.onSuccess(result);
            }

            @Override
            public void onFailure(Throwable t) {
                callback.onFailure(t);
            }
        }, tsCallBackExecutor);
    }

}
