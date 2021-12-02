package R_HTTPResponse;

public class server_par {
  public String home_dir; // Содержит текущую "домашнюю" директорию сервера
  public String default_page; // Содержит имя страницы, выводимой по умолчанию  
  public Integer max_client_con; // Количество максимально допустимого количества клиентов  
  public Integer server_port;  // Номер порта, через который работает сервер

  public server_par() {
    super();
  }
}
