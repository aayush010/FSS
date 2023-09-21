package Dropbox.demo.StorageService.Service;

import Dropbox.demo.Login.entity.User;
import Dropbox.demo.Login.repository.UserRepository;
import Dropbox.demo.StorageService.Model.UserDataInfo;
import Dropbox.demo.StorageService.repository.UserDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class UserDataService {
    @Autowired
    UserDataRepository userDataRepository;
    @Autowired
    UserRepository userInfo;

    public void store(String path, MultipartFile file) {
        User user = userInfo.getById(Integer.valueOf(path));
        UserDataInfo userDataInfo = new UserDataInfo();
        userDataInfo.setUserEmailId(user.getEmail());
        userDataInfo.setFilename(file.getOriginalFilename());
        userDataInfo.setType(file.getContentType());
        userDataInfo.setUrl("./uploads" + file.getOriginalFilename());
        userDataRepository.save(userDataInfo);
    }

}
