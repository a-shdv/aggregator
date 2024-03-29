package com.company.aggregator.exceptions;

import com.company.aggregator.exceptions.favourite.FavouriteAlreadyExistsException;
import com.company.aggregator.exceptions.favourite.FavouriteException;
import com.company.aggregator.exceptions.favourite.FavouriteNotFoundException;
import com.company.aggregator.exceptions.favourite.FavouritesIsEmptyException;
import com.company.aggregator.exceptions.user.OldPasswordIsWrongException;
import com.company.aggregator.exceptions.user.PasswordsDoNotMatchException;
import com.company.aggregator.exceptions.user.UserAlreadyExistsException;
import com.company.aggregator.exceptions.user.UserException;
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
