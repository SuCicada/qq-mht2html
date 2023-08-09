import LoggingHelper.LOGGER
import androidx.compose.runtime.mutableStateOf
import freemarker.template.Configuration
import freemarker.template.Template
//import io.ktor.server.freemarker.*
import kotlinx.coroutines.runBlocking
import org.jsoup.Jsoup
import org.jsoup.parser.Parser
import java.io.File
import java.io.StringWriter
import java.text.SimpleDateFormat
import java.util.*

val mhtFileLocation = "/Users/peng/Downloads/Lain 的 wired .mht"
//val mhtFileLocation = "/Users/peng/PROGRAM/GitHub/qq-mht2html/天天爱开车.mht"
val fileOutputLocation = "/Users/peng/PROGRAM/GitHub/qq-mht2html/output/"


val imgFileOutputFolder = "$fileOutputLocation/img"

var lineLimit = 3000

//val threadCount = mutableStateOf(Runtime.getRuntime().availableProcessors().toString())
//val lineLimitStr = Mht2Html.DEFAULT_LINE_LIMIT
val showAlert = mutableStateOf(false)
val errMsg = mutableStateOf("Error message.")
val progress = mutableStateOf(0.0F)
var noImage = mutableStateOf(false)
var noHtml = mutableStateOf(false)


fun copyStatic() {
    val outputDir = File(fileOutputLocation)
    if (!outputDir.exists()) {
        outputDir.mkdirs()
    }
    val staticFiles = arrayOf(
        "static/utils.js",
    )
    staticFiles.forEach {
        val url = object {}.javaClass.classLoader.getResource(it)
//        val staticFile = File(it)
        val filePath = url?.file ?: throw IllegalStateException("Resources directory not found!")
        val staticFile = File(filePath)
        if (!staticFile.exists()) {
            println("Static file not found: $it")
            return
        }
        staticFile.copyTo(outputDir.resolve(staticFile.name), true)
    }
}

fun run() {
    val mhtFile = File(mhtFileLocation)
    if (!mhtFile.exists() || mhtFile.isDirectory) {
        val tmpErrMsg = "Not a valid mht file location!"
        println(tmpErrMsg)
        return
    }

    val fileOutputDirFile = File(fileOutputLocation)

    if (fileOutputDirFile.exists() && !fileOutputDirFile.isDirectory) {
        val tmpErrMsg = "Output dir exists and is not a folder!"
        println(tmpErrMsg)
        return
    }

    val imgOutputFolder = fileOutputDirFile.resolve(imgFileOutputFolder)
    if (imgOutputFolder.exists() && !imgOutputFolder.isDirectory) {
        val tmpErrMsg = "Img output dir exists and is not a folder!"
        println(tmpErrMsg)
        return

    }

    var threadCount = Runtime.getRuntime().availableProcessors()
    runCatching {
//        threadCount = Integer.parseInt(threadCountStr)
        if (threadCount <= 0) threadCount = 1
    }.onFailure {
        LOGGER.info("Thread count invalid. Using core number.")
    }

    kotlin.runCatching {
//        lineLimit = Integer.parseInt(lineLimitStr)
        if (lineLimit <= 500) lineLimit = 500
    }.onFailure {
        LOGGER.info("Thread count invalid. Using default 7500.")
    }
    copyStatic()
    println("Thread count: $threadCount")
    runBlocking {
        val job =
            Mht2Html.doJob(
                mhtFileLocation,
                fileOutputLocation,
                imgOutputFolder.absolutePath,
                threadCount,
                lineLimit,
                showAlert,
                errMsg,
                progress,
                noImage = noImage.value,
                noHtml = noHtml.value
            )
        job.join()
    }
}

fun generateIndex() {
    // scan a dir: list html file
//    val htmlFiles = mutableListOf<File>()

    val directory = File(fileOutputLocation)
    if (directory.isDirectory) {
        val htmlFiles = directory.listFiles()!!
            .filter { it.isFile && it.extension == "html" && it.name != "index.html" }
            .map {
                val pair = getHtmlDateRange(it)
//                val res = """<a href="${it.name}">${it.name}</a> &nbsp; ${pair[0]} - ${pair[1]} """
                val res = mapOf(
                    "name" to it.name,
                    "dateRange" to pair // "${pair[0]} - ${pair[1]}"
                )
                res
            }
            .sortedBy { it["name"].toString()  }
            .toList()

        val cfg = Configuration(Configuration.VERSION_2_3_31)
        cfg.setClassLoaderForTemplateLoading(ClassLoader.getSystemClassLoader(), "")
        val template: Template = cfg.getTemplate("static/index.html.ftl")

        val data = mapOf("items" to htmlFiles)
        val output = StringWriter()
        template.process(data, output)

//        println(output.toString())
        // write to index.html
        val indexFile = File(fileOutputLocation, "index.html")
        indexFile.writeText(output.toString())
        println("index.html generated.")

// FreeMarkerContent("static/index.html.ftl", mapOf("items" to htmlFiles)).toString()


    } else {
        println("The specified path is not a directory.")
    }
}


fun getHtmlDateRange(html: File): Array<String> {

    val project: File = File(".").absoluteFile.parentFile
//    val html: File = File(project, "output/Lain_的_wired__001.html")

    val htmlContent = html.readText()
    val parser: Parser = Parser.htmlParser()
    val doc = Jsoup.parse(htmlContent, "", parser)

    val qqtsDivs = doc.select("div.stl-3 > div.qqts ")
    println(qqtsDivs.size)

    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    val startTimestamp = qqtsDivs[0].text().toLong()
    val endTimestamp = qqtsDivs[qqtsDivs.size - 1].text().toLong()
    val startDate = dateFormat.format(Date(startTimestamp))
    val endDate = dateFormat.format(Date(endTimestamp))
//    println(startDate)
//    println(endDate)
    return arrayOf(startDate, endDate)

}

fun main() {
//    run()
    copyStatic()
    generateIndex()
}
