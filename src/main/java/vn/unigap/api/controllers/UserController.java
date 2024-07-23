package vn.unigap.api.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import vn.unigap.api.dto.in.UserInputDTO;
import vn.unigap.api.dto.out.UserOutputDTO;
import vn.unigap.api.service.UserService;


@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/create")
    public UserOutputDTO createUser(@RequestBody UserInputDTO userInputDTO) {
        return userService.createUser(userInputDTO);
    }
}

