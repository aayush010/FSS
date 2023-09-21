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
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.ui.Model;
import java.util.List;
import java.util.stream.Collectors;

import static Dropbox.demo.StorageService.Constants.MESSAGE;
import static Dropbox.demo.StorageService.Constants.SUCCESS_MESSAGE;

@Controller
public class FileController {
    @Autowired
    FileStorageService storageService;
    @Autowired
    UserServiceImpl userService;

    @PostMapping("/v1")
    public String authenticate(@ModelAttribute("user") User user, BindingResult result) {

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

        return "redirect:/files/" + userExist.getId().toString();
    }

    @GetMapping("/files/upload/{id}")
    public String newFile(@PathVariable(value = "id") String id, Model model) {
        model.addAttribute("id", id);
        return "upload_form";
    }

    @PostMapping("/files/upload/{path}")
    public String uploadFileApi(@PathVariable(value = "path") String path, Model model, @RequestParam("file") MultipartFile file) {
        String message = "";

        try {
            storageService.save(file, path);
            message = "Uploaded the file successfully: " + file.getOriginalFilename();
            model.addAttribute("message", message);
        }catch (Exception e) {
            message = "Could not upload the file: " + file.getOriginalFilename() + ". Error: " + e.getMessage();
            model.addAttribute("message", message);
        }
        model.addAttribute("id", path);
        return "upload_form";
    }

    @GetMapping("/files/{id}")
    public String getListFiles(@PathVariable(value = "id") String id,  Model model) {

        List<FileInfo> fileInfos = storageService.loadAll(id).map(path -> {
            String filename = path.getFileName().toString();
            String url = id + '/' + filename;

            return new FileInfo(filename, url);
        }).collect(Collectors.toList());

        model.addAttribute("files", fileInfos);
        model.addAttribute("id", id);
        return "files";
    }

    @GetMapping("/files/{id}/{filename:.+}")
    public ResponseEntity<Resource> readFile(@PathVariable String filename, @PathVariable String id) {
        Resource file = storageService.load(filename, id);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }

    @GetMapping ("/files/delete/{id}/{filename:.+}")
    public String deleteFile(@PathVariable String filename, @PathVariable String id) {
        try {
            storageService.delete(filename, id);
        }catch (Exception e) {
            e.printStackTrace();
        }

        return "redirect:/files/" + id;
    }

    @PostMapping ("/files/update/{id}/{filename:.+}")
    public String updateFile(@PathVariable String id, @PathVariable String filename, @RequestParam("file") MultipartFile file, Model model) {
        storageService.delete(filename, id);
        String message = "";
        try {
            storageService.save(file, id);
            message = SUCCESS_MESSAGE + " " + file.getOriginalFilename();
            model.addAttribute(MESSAGE, message);
        }catch (Exception e) {
            message = "Could not upload the file: " + file.getOriginalFilename() + ". Error: " + e.getMessage();
            model.addAttribute(MESSAGE, message);
        }
        return "redirect:/files/" + id;
    }



}
