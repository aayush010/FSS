package Dropbox.demo.StorageService.Service;

import Dropbox.demo.Login.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Component;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import static Dropbox.demo.Constants.Constants.root;

@Component
public class FileStorageServiceImpl implements FileStorageService{

    @Autowired
    private UserDataService userDataService;

    @Override
    public void init() {
        try {
            Files.createDirectories(root);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize folder for upload!");
        }
    }

    @Override
    public void initUserFolder(User user) {
        Path userPath = Paths.get(root + "/" + user.getId().toString());
        try{
            Files.createDirectories(userPath);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize folder for user!");
        }
    }

    @Override
    public void save(MultipartFile file, String path) {
        Path newPath = Paths.get(root + "/" + path);
        try {
            Files.copy(file.getInputStream(), newPath.resolve(file.getOriginalFilename()));
            userDataService.store(path, file);
        } catch (Exception e) {
            if (e instanceof FileAlreadyExistsException) {
                throw new RuntimeException("A file of that name already exists.");
            }

            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public Resource load(String filename, String id) {
        Path newPath = Paths.get(root + "/" + id);
        try {
            Path file = newPath.resolve(filename);
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("Could not read the file!");
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Error: " + e.getMessage());
        }
    }

    @Override
    public void deleteAll() {
        FileSystemUtils.deleteRecursively(root.toFile());
    }

    @Override
    public Stream<Path> loadAll(String id) {
        Path newPath = Paths.get(root + "/" + id);
        try {
            return Files.walk(newPath, 1).filter(path -> !path.equals(newPath)).map(newPath::relativize);
        } catch (IOException e) {
            throw new RuntimeException("Could not load the files!");
        }
    }

    @Override
    public void delete(String filename, String id) {
        Path newPath = Paths.get(root + "/" + id);
        try {
            Files.delete(newPath.resolve(filename));
            userDataService.delete(filename, id);
        } catch (IOException e) {
            throw new RuntimeException("Could not delete the file!");
        }
    }
}
