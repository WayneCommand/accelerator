package ltd.inmind.accelerator.security.handler;

import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ltd.inmind.accelerator.exception.AcceleratorException;
import ltd.inmind.accelerator.model.po.DeviceToken;
import ltd.inmind.accelerator.model.vo.DataResponse;
import ltd.inmind.accelerator.service.IDeviceTokenService;
import ltd.inmind.accelerator.service.IUserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
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


@AllArgsConstructor
@Component
@Slf4j
public class AuthenticationSuccessHandler implements ServerAuthenticationSuccessHandler {

    @Autowired
    private Gson gson;

    @Autowired
    private IDeviceTokenService deviceTokenService;

    @Autowired
    private IUserService userService;

    @Override
    public Mono<Void> onAuthenticationSuccess(WebFilterExchange webFilterExchange, Authentication authentication) {
        ServerHttpResponse response = webFilterExchange.getExchange().getResponse();
        response.setStatusCode(HttpStatus.OK);
        response.getHeaders().add("Content-Type", "application/json");
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
        String ip = form.getFirst("ip");
        String deviceId = form.getFirst("deviceId"); //primary id (RV)
        String deviceName = form.getFirst("deviceName"); //edge-chromium,firefox,windows,mac
        String deviceType = form.getFirst("deviceType"); //browser,app
        String deviceVersion = form.getFirst("deviceVersion");//browser ver / system ver
        String deviceSystem = form.getFirst("deviceSystem");//windows,android
        String locationCountry = form.getFirst("locationCountry");//CN,US
        String locationCity = form.getFirst("locationCity");//SH,BJ

        DeviceToken deviceToken = new DeviceToken();
        deviceToken.setIp(ip);
        deviceToken.setDeviceId(deviceId);

        if (!StringUtils.isAnyBlank(deviceName, deviceVersion,deviceSystem)) {
            deviceToken.setDeviceName(String.format("%s %s(%s)", deviceSystem, deviceName, deviceVersion));
        } else {
            deviceToken.setDeviceName(deviceName);
        }

        deviceToken.setDeviceType(deviceName);

        if (!StringUtils.isAnyBlank(locationCity, locationCountry)){
            String location = String.format("%s-%s", locationCountry, locationCity);
            deviceToken.setLocation(location);
        }else if (StringUtils.isNotBlank(locationCountry)){
            deviceToken.setLocation(locationCountry);
        }else {
            deviceToken.setLocation("unknown");
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
