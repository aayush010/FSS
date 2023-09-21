package Dropbox.demo.Login.service;

import Dropbox.demo.Login.dto.UserDto;
import Dropbox.demo.Login.entity.User;

import java.util.List;

public interface UserService {
    void saveUser(UserDto userDto);

    User findUserByEmail(String email);

    List<UserDto> findAllUsers();
}