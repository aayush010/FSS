package Dropbox.demo.StorageService.repository;

import Dropbox.demo.StorageService.Model.FileInfo;
import Dropbox.demo.StorageService.Model.UserDataInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserDataRepository extends JpaRepository<UserDataInfo, Long> {

}
