package com.company.aggregator.exception;

import com.company.aggregator.exception.favourite.FavouriteAlreadyExistsException;
import com.company.aggregator.exception.favourite.FavouriteException;
import com.company.aggregator.exception.favourite.FavouriteNotFoundException;
import com.company.aggregator.exception.favourite.FavouritesIsEmptyException;
import com.company.aggregator.exception.user.OldPasswordIsWrongException;
import com.company.aggregator.exception.user.PasswordsDoNotMatchException;
import com.company.aggregator.exception.user.UserAlreadyExistsException;
import com.company.aggregator.exception.user.UserException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
@Slf4j
public class GlobalExceptionhandler {
    @ExceptionHandler({FavouriteAlreadyExistsException.class, FavouriteNotFoundException.class, FavouritesIsEmptyException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<?> handleFavouriteException(FavouriteException ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ex.getMessage());
    }

    @ExceptionHandler({OldPasswordIsWrongException.class, PasswordsDoNotMatchException.class})
    public void handleUserException(UserException ex, RedirectAttributes redirectAttributes) {
        log.error(ex.getMessage());
        redirectAttributes.addFlashAttribute("error", ex.getMessage());
    }

    @ExceptionHandler({UserAlreadyExistsException.class})
    public String handleUserAlreadyExistsException(UserAlreadyExistsException ex, RedirectAttributes redirectAttributes) {
        log.error(ex.getMessage());
        redirectAttributes.addFlashAttribute("error", ex.getMessage());
        return "redirect:/sign-up";
    }
}
