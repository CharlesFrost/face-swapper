package online.patologia.faceswap.controllers;

import online.patologia.faceswap.services.FaceSwappingService;
import online.patologia.faceswap.services.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Controller
public class ViewController {
    @Autowired
    private FaceSwappingService faceSwappingService;

    @GetMapping("/")
    public String showPage() {
        return "index";
    }

    @PostMapping("/")
    public String showResult(@RequestParam("file") MultipartFile file,Model model) throws InterruptedException{
        String respond = faceSwappingService.swapFace(file);
        System.out.println("respond");
        model.addAttribute("download",respond);
        return "index";
    }
}
