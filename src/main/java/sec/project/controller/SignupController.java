package sec.project.controller;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import sec.project.config.CustomUserDetailsService;
import sec.project.domain.Signup;
import sec.project.repository.SignupRepository;

@Controller
public class SignupController {

    @Autowired
    private SignupRepository signupRepository;

    @Autowired
    private CustomUserDetailsService signupDatabase;

    @RequestMapping("*")
    public String defaultMapping() {
        return "redirect:/form";
    }

    @RequestMapping(value = "/form", method = RequestMethod.GET)
    public String loadForm() {
        return "form";
    }

    @RequestMapping(value = "/done", method = RequestMethod.GET)
    public String loadDone(Model model) {
        List<Signup> signups = signupRepository
                .findAll()
                .stream()
                .filter(signup -> signup.isPublic())
                .collect(Collectors.toList());

        model.addAttribute("signups", signups);
        return "done";
    }

    @RequestMapping(value = "/form", method = RequestMethod.POST)
    public String submitForm(@RequestParam String name, @RequestParam String address, @RequestParam(value = "publicReg", required = false) boolean publicReg) {
        signupRepository.save(new Signup(name, address, publicReg));
        signupDatabase.saveUserToDatabase(name, address, publicReg);
        return "redirect:/done";
    }

    @RequestMapping(value = "/done", method = RequestMethod.POST)
    public String filter(@RequestParam String searchTerm, Model model) {
        List<Signup> signups = signupDatabase.loadUserByUsername(searchTerm);

        model.addAttribute("signups", signups);
        return "done";
    }

    @RequestMapping(value = "/signups/{id}", method = RequestMethod.GET)
    public String signup(@PathVariable(value = "id") Long id, Model model) {
        Signup signup = signupRepository.findOne(id);
        model.addAttribute("signup", signup);
        if (signup.isPublic()) {
            model.addAttribute("publicness", "Registration is public");
        } else {
            model.addAttribute("publicness", "Registration is not public");
        }

        return "signuppage";
    }

}
