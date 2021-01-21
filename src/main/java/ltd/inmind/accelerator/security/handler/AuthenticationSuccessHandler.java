package ltd.inmind.accelerator.security.handler;

import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ltd.inmind.accelerator.exception.AcceleratorException;
import ltd.inmind.accelerator.model.po.DeviceToken;
import ltd.inmind.accelerator.model.vo.DataResponse;
import ltd.inmind.accelerator.service.IDeviceTokenService;
import ltd.inmind.accelerator.service.IUserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

import static ltd.inmind.accelerator.constants.ExceptionConst.USER_NOT_EXIST;
import static ltd.inmind.accelerator.constants.SecurityConst.AUTHENTICATION_HEADER;
import static ltd.inmind.accelerator.constants.SecurityConst.TOKEN_ATTR_NAME;


@RequiredArgsConstructor
@Component
@Slf4j
public class AuthenticationSuccessHandler implements ServerAuthenticationSuccessHandler {

    private final Gson gson;

    private final IDeviceTokenService deviceTokenService;

    private final IUserService userService;

    @Override
    public Mono<Void> onAuthenticationSuccess(WebFilterExchange webFilterExchange, Authentication authentication) {
        ServerHttpResponse response = webFilterExchange.getExchange().getResponse();
        response.setStatusCode(HttpStatus.OK);
        response.getHeaders().add("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        response.getHeaders().add(AUTHENTICATION_HEADER, webFilterExchange.getExchange().getAttribute(TOKEN_ATTR_NAME));

        DataResponse dataResponse = new DataResponse()
                .success();

        String resp = gson.toJson(dataResponse);

        DataBuffer buffer = response.bufferFactory().wrap(resp.getBytes(StandardCharsets.UTF_8));

        return response.writeWith(Mono.just(buffer))
                .then(webFilterExchange.getExchange().getFormData()
                        .flatMap(this::getAccessInfo)
                        .flatMap(deviceToken -> saveDeviceToken(deviceToken,
                                webFilterExchange.getExchange().getAttribute(TOKEN_ATTR_NAME),
                                authentication.getName())));
    }

    private Mono<DeviceToken> getAccessInfo(MultiValueMap<String,String> form){
        String ip = form.getFirst("ip"); //ipv4
        String deviceId = form.getFirst("deviceId"); //primary id (RV)
        String deviceModel = form.getFirst("deviceModel"); //edge-chromium,firefox,windows,mac
        String deviceType = form.getFirst("deviceType"); //browser,app
        String deviceVersion = form.getFirst("deviceVersion");//browser ver / system ver
        String deviceSystem = form.getFirst("deviceSystem");//windows,android
        String locationCountry = form.getFirst("locationCountry");//China, United States
        String locationRegion = form.getFirst("locationRegion");//Shanghai, Virginia
        String locationCity = form.getFirst("locationCity");//Shanghai, Ashburn

        DeviceToken deviceToken = new DeviceToken();
        deviceToken.setIp(ip);
        deviceToken.setDeviceId(deviceId);

        //预设的设备名
        if (!StringUtils.isAnyBlank(deviceModel, deviceVersion,deviceSystem)) {
            deviceToken.setDeviceName(String.format("%s %s(%s)", deviceSystem, deviceModel, deviceVersion));
        } else {
            deviceToken.setDeviceName(deviceModel);
        }

        deviceToken.setDeviceModel(deviceModel);
        deviceToken.setDeviceType(deviceType);
        deviceToken.setDeviceVersion(deviceVersion);
        deviceToken.setDeviceSystem(deviceSystem);

        if (!StringUtils.isAnyBlank(locationCity, locationRegion, locationCountry)) {
            String location = locationRegion.equals(locationCity) ?
                    String.format("%s - %s", locationCountry, locationCity) :
                    String.format("%s - %s - %s", locationCountry, locationRegion, locationCity);
            deviceToken.setLocation(location);
            deviceToken.setLocationCountry(locationCountry);
            deviceToken.setLocationRegion(locationRegion);
            deviceToken.setLocationCity(locationCity);
        } else if (StringUtils.isNotBlank(locationCountry)) {
            deviceToken.setLocation(locationCountry);
            deviceToken.setLocationCountry(locationCountry);
            deviceToken.setLocationRegion("unknown");
            deviceToken.setLocationCity("unknown");
        } else {
            deviceToken.setLocation("unknown");
            deviceToken.setLocationCountry("unknown");
            deviceToken.setLocationRegion("unknown");
            deviceToken.setLocationCity("unknown");
        }

        return Mono.just(deviceToken);
    }

    private Mono<Void> saveDeviceToken(DeviceToken deviceToken, String token, String account) {

        return Mono.just(userService.getAccountByAccount(account))
                .switchIfEmpty(Mono.error(new AcceleratorException(USER_NOT_EXIST)))
                .flatMap(userAccount -> {
                    deviceToken.setToken(token);
                    deviceToken.setUId(userAccount.getUId());
                    deviceToken.setActiveCount(0);
                    deviceTokenService.saveDeviceToken(deviceToken);
                    return Mono.empty();
                });
    }

}
