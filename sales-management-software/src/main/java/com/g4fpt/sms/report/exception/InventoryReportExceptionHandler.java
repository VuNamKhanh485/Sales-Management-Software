package com.g4fpt.sms.report.exception;

import com.g4fpt.sms.common.exception.ValidationException;
import com.g4fpt.sms.report.controller.InventoryReportController;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

@ControllerAdvice(assignableTypes = InventoryReportController.class)
public class InventoryReportExceptionHandler {
    
    @ExceptionHandler(ValidationException.class)
    public String handleValidationException(
            ValidationException ex,
            RedirectAttributes redirectAttributes) {

        redirectAttributes.addFlashAttribute("errors", ex.getErrors());

        return "redirect:/report/inventory";
    }

    @ExceptionHandler(IOException.class)
    public String handleIOException(RedirectAttributes redirectAttributes) {

        redirectAttributes.addFlashAttribute(
                "error",
                "Xuất Excel thất bại.");

        return "redirect:/report/inventory";
    }
}
