package mydropbox;

import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.util.IOUtils;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import mydropbox.dto.UserInfo;
import mydropbox.s3storage.AmazonS3ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class DropboxController {

    private AmazonS3ClientService amazonS3ClientService;
    private AwsLambdaInvoke awsLambdaInvoke;

    @Autowired
    public DropboxController(AmazonS3ClientService amazonS3ClientService, AwsLambdaInvoke awsLambdaInvoke) {
        this.amazonS3ClientService = amazonS3ClientService;
        this.awsLambdaInvoke = awsLambdaInvoke;
    }

    @GetMapping("/")
    public String listUploadedFiles(Model model) {

        model.addAttribute("userInfo", new UserInfo());
        model.addAttribute("files", amazonS3ClientService.getListFiles().stream().map(S3ObjectSummary::getKey)
                                                         .collect(Collectors.toList()));

        return "uploadForm";
    }

    @PostMapping("/")
    public String handleFileUpload(@RequestParam("file") MultipartFile file,
                                   RedirectAttributes redirectAttributes) {

        this.amazonS3ClientService.uploadFileToS3Bucket(file, true);

        redirectAttributes.addFlashAttribute("message",
                                             "You successfully uploaded " + file.getOriginalFilename() + "!");

        return "redirect:/";
    }

    @DeleteMapping("/remove")
    public String deleteFile(@RequestParam("file_name") String fileName,
                             RedirectAttributes redirectAttributes) {
        this.amazonS3ClientService.deleteFileFromS3Bucket(fileName);

        redirectAttributes.addFlashAttribute("message",
                                             "file [" + fileName + "] removing request submitted successfully.");

        return "redirect:/";
    }

    @GetMapping("/{filename:.+}")
    @ResponseBody
    public ResponseEntity<byte[]> serveFile(@PathVariable String filename) throws IOException {

        S3Object s3Object = this.amazonS3ClientService.getFile(filename);
        S3ObjectInputStream objectInputStream = s3Object.getObjectContent();
        byte[] bytes = IOUtils.toByteArray(objectInputStream);
        String fileName = URLEncoder.encode(filename, "UTF-8").replaceAll("\\+", "%20");

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        httpHeaders.setContentLength(bytes.length);
        httpHeaders.setContentDispositionFormData("attachment", fileName);

        return new ResponseEntity<>(bytes, httpHeaders, HttpStatus.OK);
    }

    @PostMapping("/lambda")
    public String sendRandomFileToEmail(@ModelAttribute UserInfo userInfo,
                                        RedirectAttributes redirectAttributes) {

        List<S3ObjectSummary> s3ObjectSummaryList = amazonS3ClientService.getListFiles();
        S3ObjectSummary s3ObjectSummary = s3ObjectSummaryList.get(new Random().nextInt(s3ObjectSummaryList.size()));
        String keyToFile = s3ObjectSummary.getKey();

        String message = awsLambdaInvoke.invokeLambda(userInfo.getEmail(), "https://mydropbox-s3.s3.amazonaws.com/" + keyToFile);
        redirectAttributes.addFlashAttribute("message", message);

        return "redirect:/";
    }

}
