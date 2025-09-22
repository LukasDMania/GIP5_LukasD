package be.ucll.domain.service;

import be.ucll.application.dto.LoginDto;

public interface LoginService {
    public boolean authenticate(LoginDto loginDto);
}
