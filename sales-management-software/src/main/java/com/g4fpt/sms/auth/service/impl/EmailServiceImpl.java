package com.g4fpt.sms.auth.service.impl;

import com.g4fpt.sms.auth.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
public class EmailServiceImpl implements EmailService {
    private static final String OTP_EMAIL_SUBJECT = "Mã OTP đặt lại mật khẩu - Sales Management Software";

    private final JavaMailSender javaMailSender;
    private final String senderEmail;

    public EmailServiceImpl(
            JavaMailSender javaMailSender,
            @Value("${spring.mail.username}") String senderEmail
    ) {
        this.javaMailSender = javaMailSender;
        this.senderEmail = senderEmail;
    }

    @Override
    public void sendOtpEmail(String toEmail, String otp) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(
                    mimeMessage,
                    false,
                    StandardCharsets.UTF_8.name()
            );

            helper.setFrom(senderEmail);
            helper.setTo(toEmail);
            helper.setSubject(OTP_EMAIL_SUBJECT);
            helper.setText(buildOtpEmailContent(otp), true);

            javaMailSender.send(mimeMessage);
        } catch (MessagingException | MailException exception) {
            /*
             * Không đưa OTP hoặc chi tiết SMTP vào exception.
             * ForgotPasswordService sẽ bắt exception này và trả thông báo
             * tiếng Việt an toàn cho người dùng.
             */
            throw new IllegalStateException("Không thể gửi email OTP.", exception);
        }
    }

    private String buildOtpEmailContent(String otp) {
        return """
                <!DOCTYPE html>
                <html lang="vi">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>Mã OTP đặt lại mật khẩu</title>
                </head>
                <body style="
                    margin: 0;
                    padding: 0;
                    background-color: #f4f6f9;
                    font-family: Arial, Helvetica, sans-serif;
                    color: #212529;
                ">
                    <table role="presentation"
                           width="100%%"
                           cellspacing="0"
                           cellpadding="0"
                           border="0"
                           style="background-color: #f4f6f9; padding: 32px 12px;">
                        <tr>
                            <td align="center">
                                <table role="presentation"
                                       width="100%%"
                                       cellspacing="0"
                                       cellpadding="0"
                                       border="0"
                                       style="
                                           max-width: 600px;
                                           background-color: #ffffff;
                                           border-radius: 12px;
                                           overflow: hidden;
                                           box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
                                       ">
                                    <tr>
                                        <td style="
                                            background-color: #0d6efd;
                                            padding: 24px;
                                            text-align: center;
                                            color: #ffffff;
                                        ">
                                            <h1 style="margin: 0; font-size: 24px;">
                                                Sales Management Software
                                            </h1>
                                        </td>
                                    </tr>

                                    <tr>
                                        <td style="padding: 32px;">
                                            <h2 style="
                                                margin-top: 0;
                                                margin-bottom: 16px;
                                                font-size: 21px;
                                            ">
                                                Yêu cầu đặt lại mật khẩu
                                            </h2>

                                            <p style="line-height: 1.6; margin-bottom: 16px;">
                                                Hệ thống đã nhận được yêu cầu đặt lại mật khẩu
                                                cho tài khoản của bạn.
                                            </p>

                                            <p style="line-height: 1.6; margin-bottom: 12px;">
                                                Mã OTP xác minh của bạn là:
                                            </p>

                                            <div style="
                                                margin: 24px 0;
                                                padding: 18px;
                                                text-align: center;
                                                background-color: #eef5ff;
                                                border: 1px dashed #0d6efd;
                                                border-radius: 10px;
                                                font-size: 32px;
                                                font-weight: bold;
                                                letter-spacing: 8px;
                                                color: #0d6efd;
                                            ">
                                                %s
                                            </div>

                                            <p style="line-height: 1.6;">
                                                Mã OTP có hiệu lực trong
                                                <strong>5 phút</strong>.
                                            </p>

                                            <div style="
                                                margin-top: 20px;
                                                padding: 14px;
                                                background-color: #fff3cd;
                                                border: 1px solid #ffecb5;
                                                border-radius: 8px;
                                                color: #664d03;
                                            ">
                                                Không chia sẻ mã OTP này với bất kỳ ai,
                                                kể cả nhân viên hỗ trợ.
                                            </div>

                                            <p style="
                                                line-height: 1.6;
                                                margin-top: 24px;
                                                margin-bottom: 0;
                                                color: #6c757d;
                                            ">
                                                Nếu bạn không yêu cầu đặt lại mật khẩu,
                                                vui lòng bỏ qua email này.
                                            </p>
                                        </td>
                                    </tr>

                                    <tr>
                                        <td style="
                                            padding: 18px;
                                            text-align: center;
                                            background-color: #f8f9fa;
                                            color: #6c757d;
                                            font-size: 13px;
                                        ">
                                            Đây là email được gửi tự động từ
                                            Sales Management Software.
                                        </td>
                                    </tr>
                                </table>
                            </td>
                        </tr>
                    </table>
                </body>
                </html>
                """.formatted(otp);
    }
}
