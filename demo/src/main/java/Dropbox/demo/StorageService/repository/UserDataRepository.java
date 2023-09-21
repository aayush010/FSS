package Dropbox.demo.StorageService.repository;

import Dropbox.demo.StorageService.Model.FileInfo;
import Dropbox.demo.StorageService.Model.UserDataInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserDataRepository extends JpaRepository<UserDataInfo, Long> {

    @Query("select u from UserDataInfo u where u.userEmailId = ?1 and u.filename = ?2")
    UserDataInfo findByEmailAndFileName(String email, String filename);
}
