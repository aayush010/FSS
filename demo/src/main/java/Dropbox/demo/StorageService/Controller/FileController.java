package Dropbox.demo.StorageService.Controller;

import Dropbox.demo.Login.entity.User;
import Dropbox.demo.Login.service.UserServiceImpl;
import Dropbox.demo.StorageService.Model.FileInfo;
import Dropbox.demo.StorageService.Service.FileStorageService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.io.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

import static org.aspectj.weaver.tools.cache.SimpleCacheFactory.path;

@Controller
public class FileController {
    @Autowired
    FileStorageService storageService;
    @Autowired
    UserServiceImpl userService;

    @PostMapping("/v1")
    public String homepage(@ModelAttribute("user") User user, BindingResult result, Model model, HttpServletRequest request) {
        User userExist = userService.findUserByEmail(user.getEmail());
        if(userExist == null) {
            result.rejectValue("email", null, "User does not exist");
        }
        if(!userService.passwordMatch(user)) {
            result.rejectValue("password", null, "Password does not match");
        }
        if (result.hasErrors()) {
            return "/login";
        }

        return "redirect:/v1/files/" + userExist.getId().toString();
    }

    @GetMapping("/files/upload/{id}")
    public String newFile(@PathVariable(value = "id") String id, Model model) {
        model.addAttribute("id", id);
        return "upload_form";
    }

    @PostMapping("/files/upload/{path}")
    public String uploadFile(@PathVariable(value = "path") String path, Model model, @RequestParam("file") MultipartFile file) {
        String message = "";

        try {

            storageService.save(file, path);

            message = "Uploaded the file successfully: " + file.getOriginalFilename();
            model.addAttribute("message", message);
            //model.addAttribute("id", model.getAttribute("id"));
        }catch (Exception e) {
            message = "Could not upload the file: " + file.getOriginalFilename() + ". Error: " + e.getMessage();
            model.addAttribute("message", message);
        }
        model.addAttribute("id", path);
        return "upload_form";
    }

    @GetMapping("/v1/files/{id}")
    public String getListFiles(@PathVariable(value = "id") String id,  Model model) {

        List<FileInfo> fileInfos = storageService.loadAll(id).map(path -> {
            String filename = path.getFileName().toString();
            String url = id + '/' + filename;
            //String url = MvcUriComponentsBuilder
            //        .fromMethodName(FileController.class, "getFile", "?filename=" + path.getFileName().toString()).build().toString() + "?id=" + id;

            return new FileInfo(filename, url);
        }).collect(Collectors.toList());

        model.addAttribute("files", fileInfos);
        model.addAttribute("id", id);
        return "files";
    }

    @GetMapping("/files/{id}/{filename:.+}")
    public ResponseEntity<Resource> getFile(@PathVariable String filename, @PathVariable String id) {
        Resource file = storageService.load(filename, id);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }
}
