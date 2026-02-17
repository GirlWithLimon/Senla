package org.example.bookstore_app.util;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HibernateUtil {
        private static final Logger logger = LoggerFactory.getLogger(HibernateUtil.class);
        private static final SessionFactory sessionFactory;
        private static Session session;

        static {
            try {
                sessionFactory = new Configuration()
                        .configure("/hibernate.cfg.xml")
                        .buildSessionFactory();
                logger.info("SessionFactory создан успешно");
            } catch (Throwable ex) {
                logger.error("Ошибка при создании SessionFactory: {}", ex.getMessage());
                throw new ExceptionInInitializerError(ex);
            }
        }

        public static Session getCurrentSession() throws HibernateException {
            if (session != null && session.isOpen()) {
                return session;
            }
            session = sessionFactory.openSession();
            return session;
        }

        public static void closeSession() {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
}
