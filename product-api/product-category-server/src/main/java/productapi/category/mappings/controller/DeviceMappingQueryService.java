package productapi.category.mappings.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import productapi.category.mappings.model.DeviceCategoryPath;
import productapi.category.mappings.model.DeviceMapping;

import java.util.List;

@RestController
public class DeviceMappingQueryService {

    static Logger logger = LogManager.getLogger(DeviceMappingQueryService.class);

    @Autowired
    private DeviceMappingRepository deviceMappingRepository;

    @RequestMapping(value = "/category/addDeviceMapping", method = RequestMethod.POST, consumes = "application/json")
    public ResponseEntity<String> addDeviceMapping(@RequestBody List<DeviceMapping> deviceMappings, @RequestHeader("X-user") String userName) {
        deviceMappings.stream()
            .forEach(deviceMapping -> {
                    deviceMappingRepository.addDeviceMapping(deviceMapping);
            });
        String responseBody = "Record Added";
        logDeviceMappingAction(responseBody, deviceMappings, userName);
        return ResponseEntity.ok(responseBody);
    }

    @RequestMapping(value = "/category/deviceCategories", method = RequestMethod.GET, produces = "application/json")
    public List<DeviceCategoryPath> getDeviceCategories
            (@RequestParam("deviceId") List<String> deviceIdList, @RequestParam(name = "deviceType") String deviceType, @RequestParam(name = "language", defaultValue="fi") String lang) {
        try {
            return deviceMappingRepository.getDeviceCategoryDetails(deviceIdList, deviceType, lang);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid deviceType", e);
        }
    }

    @RequestMapping(value = "/category/updateDeviceMapping", method = RequestMethod.POST, consumes = "application/json")
    public ResponseEntity<String> updateDeviceMapping(@RequestBody List<DeviceMapping> deviceMappings, @RequestHeader("X-user") String userName) {
        deviceMappings.stream()
            .forEach(deviceMapping -> {
                deviceMappingRepository.updateDeviceMapping(deviceMapping);
            });
        String responseBody = "Record Updated";
        logDeviceMappingAction(responseBody, deviceMappings, userName);
        return ResponseEntity.ok(responseBody);
    }

    private void logDeviceMappingAction(String action, List<DeviceMapping> deviceMappings, String userName) {
        deviceMappings.forEach(deviceMapping ->
            logger.info(action + ": " +
                "modelId: " + deviceMapping.getModelid() +
                ", categoryId: " + deviceMapping.getCategoryid() +
                ", validFrom: " + deviceMapping.getValidfrom() +
                ", validTo: " + deviceMapping.getValidto() +
                ", userName: " + userName
            )
        );
    }
}
