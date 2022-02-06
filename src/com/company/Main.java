package com.company;


import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    public static void main(String[] args) {

    }

    /*
Интерфейс: сущность, которую можно отправить по почте.
У такой сущности можно получить от кого и кому направляется письмо.
*/
    public static interface Sendable {
        String getFrom();
        String getTo();
    }

    /*
Абстрактный класс,который позволяет абстрагировать логику хранения
источника и получателя письма в соответствующих полях класса.
*/
    public static abstract class AbstractSendable implements Sendable {

        protected final String from;
        protected final String to;

        public AbstractSendable(String from, String to) {
            this.from = from;
            this.to = to;
        }

        @Override
        public String getFrom() {
            return from;
        }

        @Override
        public String getTo() {
            return to;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            AbstractSendable that = (AbstractSendable) o;

            if (!from.equals(that.from)) return false;
            if (!to.equals(that.to)) return false;

            return true;
        }
    }

    /*
Письмо, у которого есть текст, который можно получить с помощью метода `getMessage`
*/
    public static class MailMessage extends AbstractSendable {

        private final String message;

        public MailMessage(String from, String to, String message) {
            super(from, to);
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            if (!super.equals(o)) return false;

            MailMessage that = (MailMessage) o;

            if (message != null ? !message.equals(that.message) : that.message != null) return false;

            return true;
        }

    }

    /*
Посылка, содержимое которой можно получить с помощью метода `getContent`
*/
    public static class MailPackage extends AbstractSendable {
        private final Package content;

        public MailPackage(String from, String to, Package content) {
            super(from, to);
            this.content = content;
        }

        public Package getContent() {
            return content;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            if (!super.equals(o)) return false;

            MailPackage that = (MailPackage) o;

            if (!content.equals(that.content)) return false;

            return true;
        }
    }

    /*
Класс, который задает посылку. У посылки есть текстовое описание содержимого и целочисленная ценность.
*/
    public static class Package {
        private final String content;
        private final int price;

        public Package(String content, int price) {
            this.content = content;
            this.price = price;
        }

        public String getContent() {
            return content;
        }

        public int getPrice() {
            return price;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Package aPackage = (Package) o;

            if (price != aPackage.price) return false;
            if (!content.equals(aPackage.content)) return false;

            return true;
        }
    }

    /*
Интерфейс, который задает класс, который может каким-либо образом обработать почтовый объект.
*/
    public static interface MailService {
        Sendable processMail(Sendable mail);
    }

    /*
    Класс, в котором скрыта логика настоящей почты
    */
    public static class RealMailService implements MailService {

        @Override
        public Sendable processMail(Sendable mail) {
            // Здесь описан код настоящей системы отправки почты.
            return mail;
        }
    }

    public static class UntrustworthyMailWorker implements MailService {

        private final MailService[] services;
        private final RealMailService realService = new RealMailService();

        public UntrustworthyMailWorker(MailService[] services) {
            this.services = services.clone();
        }

        @Override
        public Sendable processMail(Sendable mail) {
            Sendable tempResult = mail;
            for (MailService service : services) {
                tempResult = service.processMail(tempResult);
            }
            return realService.processMail(tempResult);
        }

        public RealMailService getRealMailService() {
            return realService;
        }
    }

    public static class Spy implements MailService {

        private final Logger logger;
        public static final String AUSTIN_POWERS = "Austin Powers";

        public Spy(Logger logger) {
            this.logger = logger;
        }

        @Override
        public Sendable processMail(Sendable mail) {
            if (!(mail instanceof MailMessage))
                return mail;
            MailMessage mailMessage = (MailMessage) mail;
            if (mailMessage.from.equals(AUSTIN_POWERS) || mailMessage.to.equals(AUSTIN_POWERS)) {
                String msg = String.format(
                        "Detected target mail correspondence: from %s to %s \"%s\"",
                        mailMessage.from,
                        mailMessage.to,
                        mailMessage.message);
                logger.log(Level.WARNING, msg);
            } else {
                String msg = String.format(
                        "Usual correspondence: from %s to %s",
                        mailMessage.from,
                        mailMessage.to
                );
                logger.log(Level.INFO, msg);
            }
            return mail;
        }
    }

    public static class Thief implements MailService {

        private final int minCost;
        private int stolenValue;

        public Thief(int minCost) {
            this.minCost = minCost;
        }

        @Override
        public Sendable processMail(Sendable mail) {
            if (!(mail instanceof MailPackage))
                return mail;
            MailPackage mailPackage = (MailPackage) mail;
            Package pack = mailPackage.getContent();
            if (pack == null || pack.getPrice() < minCost)
                return mail;
            stolenValue += pack.getPrice();
            String newContent = String.format( "stones instead of %s", pack.getContent());
            Package newPack = new Package(newContent, 0);
            MailPackage newMail = new MailPackage(mailPackage.from, mailPackage.to, newPack);
            return newMail;
        }

        public int getStolenValue() {
            return stolenValue;
        }
    }

    public static class Inspector implements MailService {

        public static final String WEAPONS = "weapons";
        public static final String BANNED_SUBSTANCE = "banned substance";
        public static final String STONES = "stones";

        @Override
        public Sendable processMail(Sendable mail) {
            if (!(mail instanceof MailPackage))
                return mail;
            MailPackage mailPackage = (MailPackage) mail;
            if (mailPackage.getContent() == null || mailPackage.getContent().getContent() == null)
                return mail;
            String content = mailPackage.getContent().getContent();
            if (content.contains(WEAPONS) || content.contains(BANNED_SUBSTANCE))
                throw new IllegalPackageException();
            if (content.contains(STONES))
                throw new StolenPackageException();
            return mail;
        }
    }

    public static class IllegalPackageException extends RuntimeException {

        public IllegalPackageException() {};

        public IllegalPackageException(String msg) {
            super(msg);
        }

        public IllegalPackageException(String msg, Throwable cause) {
            super(msg, cause);
        }

    }

    public static class StolenPackageException extends RuntimeException {

        public StolenPackageException() {};

        public StolenPackageException(String msg) {
            super(msg);
        }

        public StolenPackageException(String msg, Throwable cause) {
            super(msg, cause);
        }
    }




}

