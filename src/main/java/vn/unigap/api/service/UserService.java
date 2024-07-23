package vn.unigap.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import vn.unigap.api.dto.in.UserInputDTO;
import vn.unigap.api.dto.out.UserOutputDTO;
import vn.unigap.api.entity.User;
import vn.unigap.api.repository.UserRepository;


@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public UserOutputDTO createUser(UserInputDTO userInputDTO) {
        User user = User.builder()
                .name(userInputDTO.getName())
                .email(userInputDTO.getEmail())
                .password(userInputDTO.getPassword())
                .build();
        userRepository.save(user);
        return convertToOutputDTO(user);
    }

    private UserOutputDTO convertToOutputDTO(User user) {
        return UserOutputDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }
}


