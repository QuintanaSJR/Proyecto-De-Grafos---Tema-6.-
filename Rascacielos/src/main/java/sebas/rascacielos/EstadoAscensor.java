package sebas.rascacielos;

//Clase estado para vereificar en dónde está el ascensor y mostrarlo

import java.util.Objects;

public class EstadoAscensor {
    private int idAscensor;
    private int pisoActual;

    public EstadoAscensor(int idAscensor, int pisoActual) {
        this.idAscensor = idAscensor;
        this.pisoActual = pisoActual;
    }

    public int getPisoActual() { return pisoActual; }
    public int getIdAscensor() { return idAscensor; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EstadoAscensor that = (EstadoAscensor) o;
        return pisoActual == that.pisoActual && idAscensor == that.idAscensor;
    }

    @Override
    public int hashCode() {
        return Objects.hash(idAscensor, pisoActual);
    }

    @Override
    public String toString() {
        return "Ascensor " + idAscensor + " [Piso " + pisoActual + "]";
    }
}