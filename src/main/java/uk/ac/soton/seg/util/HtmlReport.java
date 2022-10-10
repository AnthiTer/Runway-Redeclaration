package uk.ac.soton.seg.util;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.function.BiFunction;
import java.util.function.Function;

import javax.imageio.ImageIO;
import javax.imageio.stream.FileCacheImageOutputStream;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import uk.ac.soton.seg.model.Airport;
import uk.ac.soton.seg.model.Calc;
import uk.ac.soton.seg.model.Calc.CalculatedParameters;
import uk.ac.soton.seg.model.Calc.FlightModes;
import uk.ac.soton.seg.model.RunwayParameterProvider;

public class HtmlReport {
    private static Logger log = LogManager.getLog(HtmlReport.class.getName());

    private Airport airport;
    private BufferedImage topView;
    private BufferedImage sideView;
    private CalculatedParameters currentParameters;

    private FlightModes calculationMode;

    public HtmlReport(Airport airport, BufferedImage topView, BufferedImage sideView,
            CalculatedParameters currentParameters, Calc.FlightModes calculationMode) {
        this.airport = airport;
        this.topView = topView;
        this.sideView = sideView;
        this.currentParameters = currentParameters;
        this.calculationMode = calculationMode;
    }

    public void write(File f) throws IOException {
        var runway = currentParameters.getRunway();
        log.info(String.format(": Writing report to %s", f));
        Document doc;
        try {
            doc = Jsoup.parse(this.getClass().getResourceAsStream("report_template.html"), null, "");
        } catch (IOException e) {
            log.error("error loading resource report_template.html: " + e.getMessage());
            throw e;
        }

        BiFunction<String, String, Element> setIdText = (id, content) -> doc.getElementById(id).text(content);
        Function<RunwayParameterProvider, String> formatParams = (params) -> String.format(
                "TORA: %d\nTODA: %d\nASDA: %d\n LDA: %d\n", params.getTora(), params.getToda(), params.getAsda(),
                params.getLda());

        setIdText.apply("title_timestamp", DateTimeFormatter.RFC_1123_DATE_TIME.format(ZonedDateTime.now()));
        setIdText.apply("airport_data", airport.getPrettyName());
        setIdText.apply("runway_data", runway.prettyName());
        setIdText.apply("calculation_mode", calculationMode.toString());
        setIdText.apply("old_params", formatParams.apply(runway));
        setIdText.apply("new_params", formatParams.apply(currentParameters));

        // setIdText.apply("obstacle_data",);
        setIdText.apply("tora_explanation", currentParameters.getToraExplanation());
        setIdText.apply("toda_explanation", currentParameters.getTodaExplanation());
        setIdText.apply("asda_explanation", currentParameters.getAsdaExplanation());
        setIdText.apply("lda_explanation", currentParameters.getLdaExplanation());

        // Images
        Function<BufferedImage, String> imageToBase64 = (image) -> {
            var outStream = new ByteArrayOutputStream();
            try {
                ImageIO.write(image, "PNG", new FileCacheImageOutputStream(outStream, null));
            } catch (Exception e) {
                log.error("error encoding image, skipping");
                return "";
            }
            return Base64.getMimeEncoder().encodeToString(outStream.toByteArray());
        };

        var topEncoded = imageToBase64.apply(topView);
        doc.getElementById("top_image").attr("src", "data:image/png;base64," + topEncoded);

        var sideEncoded = imageToBase64.apply(sideView);
        doc.getElementById("side_image").attr("src", "data:image/png;base64," + sideEncoded);

        try (FileWriter writer = new FileWriter(f)) {
            writer.write(doc.outerHtml());
        } catch (IOException e) {
            log.error("error writing report to " + f.toString() + ": " + e.getMessage());
            throw e;
        }
    }

}
