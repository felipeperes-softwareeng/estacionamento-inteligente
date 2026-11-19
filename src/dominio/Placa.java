package dominio;
/** RN06: placa antiga ou Mercosul. Valor imutável e validado. */
public record Placa(String valor) {
    public Placa {
        if (valor == null || (!valor.matches("[A-Z]{3}[0-9][A-Z][0-9]{2}")
                && !valor.matches("[A-Z]{3}[0-9]{4}"))) {
            throw new PlacaInvalidaException(valor);
        }
    }
}
