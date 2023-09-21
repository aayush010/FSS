package Dropbox.demo.Login.service;

import Dropbox.demo.Login.dto.UserDto;
import Dropbox.demo.Login.entity.User;
import Dropbox.demo.Login.repository.UserRepository;
import Dropbox.demo.StorageService.Service.FileStorageServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserServiceImpl implements UserService {
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private FileStorageServiceImpl fileStorageService;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void saveUser(UserDto userDto) {
        User user = new User();
        user.setName(userDto.getFirstName() + " " + userDto.getLastName());
        user.setEmail(userDto.getEmail());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));

        User user1 = userRepository.save(user);
        fileStorageService.initUserFolder(user1);
    }

    @Override
    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public List<UserDto> findAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream().map((user) -> convertEntityToDto(user))
                .collect(Collectors.toList());
    }

    private UserDto convertEntityToDto(User user) {
        UserDto userDto = new UserDto();
        String[] name = user.getName().split(" ");
        userDto.setFirstName(name[0]);
        userDto.setLastName(name[1]);
        userDto.setEmail(user.getEmail());
        return userDto;
    }

    public boolean passwordMatch(User user) {
        User user1 = userRepository.findByEmail(user.getEmail());
        BCryptPasswordEncoder bc = new BCryptPasswordEncoder();
        boolean passChecker = bc.matches(user.getPassword(),user1.getPassword());
        if(passChecker){
            return true;
        }
        return false;
    }
}
