package livro_ms.demo.exception;

public record ErroResposta(String erro) {
    public static ErroResposta de(String mensagem) {
        return new ErroResposta(mensagem);
    }
}
