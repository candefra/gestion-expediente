package com.deptoeconomico.expedientes.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.deptoeconomico.expedientes.model.EstadoDocumento;
import com.deptoeconomico.expedientes.model.EstadoNota;
import com.deptoeconomico.expedientes.model.Expediente;
import com.deptoeconomico.expedientes.model.Nota;
import com.deptoeconomico.expedientes.repository.NotaRepository;

@Service
public class NotaService {

   private static final DateTimeFormatter FORMATO_FECHA =
            DateTimeFormatter.ofPattern("d 'de' MMMM 'de' yyyy", new Locale("es", "AR"));

    // Medidas tomadas del modelo real (EjemploNota.docx), convertidas a puntos
    private static final float ANCHO_PAGINA = PDRectangle.A4.getWidth();   // 595.28pt
    private static final float ALTO_PAGINA = PDRectangle.A4.getHeight();   // 841.89pt
    private static final float MARGEN_IZQ = 85f;
    private static final float MARGEN_DER = 57f;
    private static final float MARGEN_SUP = 92f;
    private static final float MARGEN_INF = 71f;
    private static final float ANCHO_UTIL = ANCHO_PAGINA - MARGEN_IZQ - MARGEN_DER;

    private static final float ALTO_LINEA_CUERPO = 15.8f; // 11pt con interlineado 1.2
    private static final float ALTO_LINEA_ENCABEZADO = 13f;

    // El modelo usa "Century Gothic", que no viene incluida en PDFBox por
    // defecto. Usamos Helvetica como reemplazo hasta que se pueda embeber
    // la tipografia real (por ejemplo tomandola de C:\Windows\Fonts en el
    // servidor donde corre la app).
    private static final PDType1Font FUENTE = PDType1Font.HELVETICA;
    private static final PDType1Font FUENTE_NEGRITA = PDType1Font.HELVETICA_BOLD;
    
    private final NotaRepository notaRepository;
  
    public NotaService(NotaRepository notaRepository) {
        this.notaRepository = notaRepository;
    }
    
                
        public Optional<Nota> buscarBorrador(String numeroTramite) {
            return notaRepository.findFirstByExpedienteNumeroTramiteAndEstadoDocumento(
                    numeroTramite,
                    EstadoDocumento.BORRADOR);
        }
        
    public List<Nota> listarTodas() {
        return notaRepository.findAll();
    }

    @Transactional
    public Nota actualizarEstado(Long id, EstadoNota estado) {
        Nota nota = notaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No existe la nota " + id));
        nota.setEstado(estado);
        return notaRepository.save(nota);
    }
    
    private void escribirDerecha(PDPageContentStream cs,
            String texto,
            float y,
            PDType1Font fuente,
            float tamanio) throws IOException {

float anchoTexto = fuente.getStringWidth(texto) / 1000 * tamanio;

float x = ANCHO_PAGINA - MARGEN_DER - anchoTexto;

escribirTexto(cs, texto, x, y, fuente, tamanio);
}

    public byte[] generarPdf(Nota nota) throws IOException {
        Expediente expediente = nota.getExpediente();

        try (PDDocument documento = new PDDocument();
             ByteArrayOutputStream salida = new ByteArrayOutputStream()) {

            PDPage pagina = new PDPage(PDRectangle.A4);
            documento.addPage(pagina);
            PDPageContentStream cs = new PDPageContentStream(documento, pagina);

            // --- Logo (arriba a la izquierda) ---
            PDImageXObject logo = cargarImagen(documento, "pdf-assets/logo-ater.png");
            float logoAncho = 118f;
            float logoAlto = logoAncho * logo.getHeight() / logo.getWidth();
            float logoY = ALTO_PAGINA - 50 - logoAlto;
            cs.drawImage(logo, MARGEN_IZQ, logoY, logoAncho, logoAlto);

            float y = logoY - ALTO_LINEA_ENCABEZADO * 2;

         // --- Título ---
            escribirDerecha(cs,
                    nota.getTituloDocumento(),
                    y,
                    FUENTE_NEGRITA,
                    11);

            y -= ALTO_LINEA_ENCABEZADO * 1.5f;

            // --- Referencia ---
            escribirDerecha(cs,
                    "Ref.: Expediente N° " + expediente.getNumeroTramite(),
                    y,
                    FUENTE_NEGRITA,
                    10);

            y -= ALTO_LINEA_ENCABEZADO;

            // --- Fecha ---
            escribirDerecha(cs,
                    "PARANÁ, " + nota.getFecha().format(FORMATO_FECHA),
                    y,
                    FUENTE_NEGRITA,
                    10);

            y -= ALTO_LINEA_ENCABEZADO * 2.2f;

  
            // --- Bloque destinatario (cargo / area / nombre / "SU DESPACHO") ---
            if (nota.getCargo() != null && !nota.getCargo().isBlank()) {
                escribirTexto(cs, "SEÑOR " + nota.getCargo().toUpperCase(), MARGEN_IZQ, y, FUENTE_NEGRITA, 11);
                y -= ALTO_LINEA_ENCABEZADO;
            }
            if (nota.getArea() != null && !nota.getArea().isBlank()) {
                escribirTexto(cs, "DE " + nota.getArea().toUpperCase(), MARGEN_IZQ, y, FUENTE_NEGRITA, 11);
                y -= ALTO_LINEA_ENCABEZADO;
            }
            if (nota.getNombreDestinatario() != null && !nota.getNombreDestinatario().isBlank()) {
                escribirTexto(cs, nota.getNombreDestinatario(), MARGEN_IZQ, y, FUENTE_NEGRITA, 11);
                y -= ALTO_LINEA_ENCABEZADO;
            }
            y -= ALTO_LINEA_ENCABEZADO * 0.5f;
            y = escribirSubrayado(cs, "SU DESPACHO", MARGEN_IZQ, y, FUENTE_NEGRITA, 11);
            y -= ALTO_LINEA_ENCABEZADO * 2.5f;

            // --- Cuerpo (justificado, con sangria de primera linea) ---
            // Sanitizamos los saltos de linea: un <textarea> puede mandar "\r\n"
            // (Windows) y el "\r" (retorno de carro) no existe en la fuente
            // Helvetica/WinAnsiEncoding, lo que rompia getStringWidth().
            String cuerpo = nota.getCuerpo() != null ? nota.getCuerpo() : "";
            cuerpo = cuerpo.replace("\r\n", "\n").replace("\r", "\n");

            boolean primerParrafo = true;
            for (String parrafo : cuerpo.split("\n")) {
                float sangria = primerParrafo || true ? 20f : 0f; // sangria en cada parrafo, como el modelo
                List<String> lineas = partirEnLineas(parrafo, FUENTE, 11, ANCHO_UTIL - sangria);
                for (int i = 0; i < lineas.size(); i++) {
                    if (y < MARGEN_INF + ALTO_LINEA_CUERPO * 4) {
                        dibujarPie(documento, cs);
                        cs.close();
                        pagina = new PDPage(PDRectangle.A4);
                        documento.addPage(pagina);
                        cs = new PDPageContentStream(documento, pagina);
                        y = ALTO_PAGINA - MARGEN_SUP;
                    }
                    float x = (i == 0) ? MARGEN_IZQ + sangria : MARGEN_IZQ;
                    escribirTexto(cs, lineas.get(i), x, y, FUENTE, 11);
                    y -= ALTO_LINEA_CUERPO;
                }
                y -= ALTO_LINEA_CUERPO * 0.4f;
                primerParrafo = false;
            }

            // --- Pie de pagina en la ultima hoja ---
            dibujarPie(documento, cs);
            cs.close();

            documento.save(salida);
            return salida.toByteArray();
        }
    }

    private void dibujarPie(PDDocument documento, PDPageContentStream cs) throws IOException {
        PDImageXObject pie = cargarImagen(documento, "pdf-assets/pie-ater.png");
        float pieAncho = ANCHO_UTIL;
        float pieAlto = pieAncho * pie.getHeight() / pie.getWidth();
        cs.drawImage(pie, MARGEN_IZQ, MARGEN_INF - pieAlto - 10, pieAncho, pieAlto);
    }

    private PDImageXObject cargarImagen(PDDocument documento, String rutaClasspath) throws IOException {
        try (InputStream in = new ClassPathResource(rutaClasspath).getInputStream()) {
            byte[] bytes = in.readAllBytes();
            return PDImageXObject.createFromByteArray(documento, bytes, rutaClasspath);
        }
    }

    private void escribirTexto(PDPageContentStream cs, String texto, float x, float y,
                                PDType1Font fuente, float tamanio) throws IOException {
        cs.beginText();
        cs.setFont(fuente, tamanio);
        cs.newLineAtOffset(x, y);
        cs.showText(texto);
        cs.endText();
    }



    private float escribirSubrayado(PDPageContentStream cs, String texto, float x, float y,
                                     PDType1Font fuente, float tamanio) throws IOException {
        escribirTexto(cs, texto, x, y, fuente, tamanio);
        float ancho = fuente.getStringWidth(texto) / 1000 * tamanio;
        dibujarSubrayado(cs, x, y, ancho);
        return y;
    }

    private void dibujarSubrayado(PDPageContentStream cs, float x, float y, float ancho) throws IOException {
        cs.setLineWidth(0.7f);
        cs.moveTo(x, y - 2);
        cs.lineTo(x + ancho, y - 2);
        cs.stroke();
    }
    
    public List<Nota> listarPorExpediente(String numeroTramite) {
        return notaRepository.findByExpedienteNumeroTramiteOrderByFechaDesc(numeroTramite)
                .stream()
                .filter(n -> n.getEstadoDocumento() == EstadoDocumento.FINALIZADO)
                .toList();
    }
    
    public Nota buscarPorId(Long id) {
        return notaRepository.findById(id)
                .orElseThrow(() ->
                    new IllegalArgumentException("No existe la nota " + id));
    }
    
    public boolean tieneNotas(String numeroTramite) {
        return notaRepository.existsByExpedienteNumeroTramite(numeroTramite);
    }
    
    private List<String> partirEnLineas(String texto, PDType1Font fuente, float tamanio, float anchoMaximo)
            throws IOException {
        List<String> lineas = new ArrayList<>();
        if (texto.isBlank()) {
            lineas.add("");
            return lineas;
        }
        StringBuilder lineaActual = new StringBuilder();
        for (String palabra : texto.split(" ")) {
            String propuesta = lineaActual.isEmpty() ? palabra : lineaActual + " " + palabra;
            float ancho = fuente.getStringWidth(propuesta) / 1000 * tamanio;
            if (ancho > anchoMaximo && !lineaActual.isEmpty()) {
                lineas.add(lineaActual.toString());
                lineaActual = new StringBuilder(palabra);
            } else {
                lineaActual = new StringBuilder(propuesta);
            }
        }
        if (!lineaActual.isEmpty()) {
            lineas.add(lineaActual.toString());
        }
        return lineas;
    }
    
    @Transactional
    public Nota guardar(Nota nota) {
        // El borrador NO recibe número. El número se asigna recién al finalizar.
        return notaRepository.save(nota);
    }

    @Transactional
    public Nota finalizar(Nota nota) {
        if (nota.getNumero() == null) {
            int anio = nota.getFecha().getYear();
            int maximo = notaRepository.buscarMaximoNumero(nota.getTipo(), anio);
            nota.setNumero(maximo + 1);
        }
        nota.setEstadoDocumento(EstadoDocumento.FINALIZADO);
        return notaRepository.save(nota);
    }
    
   
}