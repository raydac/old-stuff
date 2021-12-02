package com.igormaznitsa.R_HTTP;

public class server_par {
  public String home_dir; // Содержит текущую "домашнюю" директорию сервера
  public String default_page; // Содержит имя страницы, выводимой по умолчанию
  public Integer max_client_con; // Количество максимально допустимого количества клиентов
  public Integer server_port;  // Номер порта, через который работает сервер
  public int Time_delay;
  public GlobalServerParams ServerParams; // Глобальные параметры для данного сервера

  public server_par() {
    super();
  }

  public synchronized int GetTimeDelay() {
    return Time_delay;
  }
}
