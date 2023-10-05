import java.io.Serializable;
import java.time.LocalTime;

class Veiculo implements Serializable {
  private String placa;
  private String cor;
  private int numeroApartamento;
  private LocalTime horaEntrada;
  private int faixaEstacionamento;

  public Veiculo(String placa, String cor, int numeroApartamento, LocalTime horaEntrada) {
    this.placa = placa;
    this.cor = cor;
    this.numeroApartamento = numeroApartamento;
    this.horaEntrada = horaEntrada;
  }

  public String getPlaca() {
    return placa;
  }

  public String getCor() {
    return cor;
  }

  public int getNumeroApartamento() {
    return numeroApartamento;
  }

  public LocalTime getHoraEntrada() {
    return horaEntrada;
  }

  public int getFaixaEstacionamento() {
    return faixaEstacionamento;
  }

  public void setFaixaEstacionamento(int faixaEstacionamento) {
    this.faixaEstacionamento = faixaEstacionamento;
  }

  @Override
  public String toString() {
    return "Placa: " + placa + ", Cor: " + cor + ", Apartamento: " + numeroApartamento;
  }

}