package be.ucll.application.events;

import be.ucll.application.dto.LoginDto;

public record LoginRequestedEvent(LoginDto loginDto) {
}
