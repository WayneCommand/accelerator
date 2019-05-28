package ltd.inmind.user_service.service.impl;

import ltd.inmind.user_service.dao.Oauth2ClientDao;
import ltd.inmind.user_service.model.Oauth2Client;
import ltd.inmind.user_service.service.Oauth2ClientService;
import ltd.inmind.user_service.utils.UUIDUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class Oauth2ClientServiceImpl implements Oauth2ClientService {

    @Autowired
    private Oauth2ClientDao oauth2ClientDao;


    @Override
    public boolean newClient(Oauth2Client oauth2Client) {
        try {

            //生成clientID clientSecret
            String clientId = UUIDUtil.generateShortUuid();
            String clientSecret = ""; //TODO 怎么生成secret

            oauth2ClientDao.insert(oauth2Client);
        } catch (Exception e) {

            //LOG
            return false;
        }

        return true;
    }

}
