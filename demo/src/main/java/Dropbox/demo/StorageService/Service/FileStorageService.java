package Dropbox.demo.StorageService.Service;

import Dropbox.demo.Login.entity.User;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.stream.Stream;

public interface FileStorageService {
    public void init();

    public void initUserFolder(User user);

    public void save(MultipartFile file, String l);

    public Resource load(String filename, String id);

    public void deleteAll();

    public Stream<Path> loadAll(String id);
}
