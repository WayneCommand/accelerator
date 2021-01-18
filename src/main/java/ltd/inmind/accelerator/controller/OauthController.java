package ltd.inmind.accelerator.controller;

import lombok.RequiredArgsConstructor;
import ltd.inmind.accelerator.model.oauth2.Oauth2Client;
import ltd.inmind.accelerator.model.vo.DataResponse;
import ltd.inmind.accelerator.service.Oauth2AccessTokenService;
import ltd.inmind.accelerator.service.Oauth2ClientService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/oauth/2")
public class OauthController {

    private final Oauth2ClientService clientService;

    private final Oauth2AccessTokenService accessTokenService;

    @GetMapping("/clients")
    public DataResponse list(){

        return new DataResponse()
                .success()
                .data("list", clientService.list());
    }

    @PostMapping("/create_client")
    public DataResponse createClient(){
        Oauth2Client oauth2Client = new Oauth2Client();
        oauth2Client.setClientName("micro_app");
        oauth2Client.setCallbackUrl("http://micro.app/callbacl");
        oauth2Client.setDescription("this is test client.");

        clientService.newClient(oauth2Client);

        return new DataResponse().success();
    }



}
