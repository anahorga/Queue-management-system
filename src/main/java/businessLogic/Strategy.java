package businessLogic;

import model.Client;
import model.Coada;

import java.util.List;

public interface Strategy {

    public void addTask(List<Coada> cozi, Client c);

}
