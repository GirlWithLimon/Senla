package org.example.bookstore_app.controller;

import org.example.bookstore_app.model.Stok;

import java.io.*;
import java.util.logging.Logger;

public class DataSave {
        private static final Logger logger = Logger.getLogger(DataSave.class.getName());
        private static final String DATA_FILE = "bookstore_data.out";
        private static DataSave instance;

        public DataSave() {}

        public static DataSave getInstance() {
            if (instance == null) {
                instance = new DataSave();
            }
            return instance;
        }

        public void saveState(Stok stok) {
            try (ObjectOutputStream oos = new ObjectOutputStream(
                    new FileOutputStream(DATA_FILE))) {
                oos.writeObject(stok);
                logger.info("Состояние программы успешно сохранено в файл: " + DATA_FILE);
            } catch (IOException e) {
                logger.severe("Ошибка при сохранении состояния: " + e.getMessage());
                System.out.println("Ошибка при сохранении данных: " + e.getMessage());
            }
        }

        public Stok loadState() {
            File file = new File(DATA_FILE);
            if (!file.exists()) {
                logger.info("Файл данных не найден. Будет создана новая база данных.");
                return new Stok();
            }

            try (ObjectInputStream ois = new ObjectInputStream(
                    new FileInputStream(DATA_FILE))) {
                Stok stok = (Stok) ois.readObject();
                logger.info("Состояние программы успешно загружено из файла: " + DATA_FILE);
                return stok;
            } catch (IOException | ClassNotFoundException e) {
                logger.severe("Ошибка при загрузке состояния: " + e.getMessage());
                System.out.println("Ошибка при загрузке данных. Создана новая база данных.");
                return new Stok();
            }
        }
}

