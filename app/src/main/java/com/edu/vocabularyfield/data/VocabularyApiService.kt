package com.edu.vocabularyfield.data

import com.google.gson.Gson
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

interface VocabularyApiService {

    @GET("api/ielts/vocabulary-sets")
    suspend fun searchIeltsVocabularySets(): List<VocabularyBook>

    @GET("api/ielts/words/{bookId}")
    suspend fun getWordsForBook(@Path("bookId") bookId: String): List<VocabularyWord>

    companion object {
        fun create(): VocabularyApiService {
            val client = OkHttpClient.Builder()
                .addInterceptor(IeltsMockInterceptor())
                .build()

            return Retrofit.Builder()
                .baseUrl("https://api.vocabfield.edu/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(VocabularyApiService::class.java)
        }
    }
}

class IeltsMockInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        Thread.sleep(1000)

        val path = chain.request().url.encodedPath
        val json = if (path.contains("words")) {
            val bookId = path.substringAfterLast("words/").trimEnd('/')
            Gson().toJson(vocabularyWordsMap[bookId] ?: emptyList<VocabularyWord>())
        } else {
            Gson().toJson(ieltsVocabularySets)
        }

        return Response.Builder()
            .code(200)
            .message("OK")
            .request(chain.request())
            .protocol(Protocol.HTTP_1_1)
            .body(json.toResponseBody("application/json".toMediaType()))
            .build()
    }

    private val vocabularyWordsMap = mapOf(
        "1" to listOf(
            VocabularyWord("advocate", "To publicly recommend or support a particular cause, policy, or course of action."),
            VocabularyWord("optimize", "To make the best or most effective use of a situation or resource."),
            VocabularyWord("utilize", "To make practical and effective use of something."),
            VocabularyWord("xenophobia", "Dislike of or prejudice against people from other countries."),
            VocabularyWord("ambiguous", "Open to more than one interpretation; not having one obvious meaning."),
            VocabularyWord("comprehensive", "Including or dealing with all or nearly all elements or aspects of something."),
            VocabularyWord("diminish", "To make or become less in size, importance, or value."),
            VocabularyWord("empirical", "Based on observation or experience rather than theory or pure logic."),
            VocabularyWord("fluctuate", "To rise and fall irregularly in number or amount."),
            VocabularyWord("hypothesis", "A proposed explanation made on the basis of limited evidence as a starting point.")
        ),
        "2" to listOf(
            VocabularyWord("abundant", "Existing or available in large quantities; plentiful."),
            VocabularyWord("benevolent", "Well-meaning and kindly; showing goodwill."),
            VocabularyWord("catalyst", "A substance or person that causes or accelerates a change or event."),
            VocabularyWord("detriment", "The state of being harmed or damaged; a cause of harm or damage."),
            VocabularyWord("eloquent", "Fluent or persuasive in speaking or writing."),
            VocabularyWord("feasible", "Possible and practical to do easily or conveniently."),
            VocabularyWord("gregarious", "Fond of company; sociable and outgoing."),
            VocabularyWord("hinder", "To create difficulties, resulting in delay or obstruction."),
            VocabularyWord("inevitable", "Certain to happen; unavoidable and bound to occur."),
            VocabularyWord("juxtapose", "To place close together for contrasting effect.")
        ),
        "3" to listOf(
            VocabularyWord("meticulous", "Showing great attention to detail; very careful and precise."),
            VocabularyWord("nuance", "A subtle difference in or shade of meaning, expression, or sound."),
            VocabularyWord("obsolete", "No longer produced or used; out of date and replaced by something newer."),
            VocabularyWord("paradigm", "A typical example or pattern of something; a model or framework."),
            VocabularyWord("quintessential", "Representing the most perfect or typical example of a quality or class."),
            VocabularyWord("resilient", "Able to withstand or recover quickly from difficult conditions."),
            VocabularyWord("scrutinize", "To examine or inspect closely and thoroughly with critical attention."),
            VocabularyWord("tangible", "Clear and definite; real and able to be touched or felt."),
            VocabularyWord("ubiquitous", "Present, appearing, or found everywhere at the same time."),
            VocabularyWord("volatile", "Liable to change rapidly and unpredictably, especially for the worse.")
        ),
        "4" to listOf(
            VocabularyWord("articulate", "Having or showing the ability to speak fluently and coherently."),
            VocabularyWord("convey", "To transport, carry, or communicate a message or idea."),
            VocabularyWord("elaborate", "To develop or present in detail; involving many carefully arranged parts."),
            VocabularyWord("illustrate", "To explain or make something clear by using examples or comparisons."),
            VocabularyWord("perceive", "To become aware or conscious of something through the senses."),
            VocabularyWord("profound", "Very great or intense; having deep insight or understanding."),
            VocabularyWord("reiterate", "To say something again or a number of times for emphasis or clarity."),
            VocabularyWord("spontaneous", "Performed or occurring as a result of a sudden impulse without planning."),
            VocabularyWord("vague", "Of uncertain, indefinite, or unclear character or meaning."),
            VocabularyWord("vivid", "Producing powerful feelings or strong, clear images in the mind.")
        ),
        "5" to listOf(
            VocabularyWord("assert", "To state a fact or belief confidently and forcefully."),
            VocabularyWord("coherent", "Logical and consistent; forming a unified, clear whole."),
            VocabularyWord("contrast", "To compare in such a way as to emphasize differences."),
            VocabularyWord("depict", "To show or represent by a drawing, painting, or description."),
            VocabularyWord("enhance", "To intensify, increase, or further improve the quality or value of."),
            VocabularyWord("fundamental", "Forming a necessary base or core; of central importance."),
            VocabularyWord("imply", "To strongly suggest the truth or existence of something not expressly stated."),
            VocabularyWord("justify", "To show or prove to be right, reasonable, or warranted."),
            VocabularyWord("moreover", "As a further matter; besides what has already been mentioned."),
            VocabularyWord("subsequent", "Coming after something in time; following a previous event.")
        ),
        "6" to listOf(
            VocabularyWord("adjacent", "Next to or adjoining something else; close or near."),
            VocabularyWord("concurrent", "Existing, happening, or done at the same time."),
            VocabularyWord("derive", "To obtain something from a specified source or origin."),
            VocabularyWord("excerpt", "A short extract taken from a piece of writing, music, or film."),
            VocabularyWord("infer", "To deduce or conclude information from evidence and reasoning."),
            VocabularyWord("notable", "Worthy of attention or notice; remarkable and significant."),
            VocabularyWord("paraphrase", "To express the meaning of using different words for clarity."),
            VocabularyWord("relevant", "Closely connected or appropriate to what is being done or considered."),
            VocabularyWord("summarize", "To give a brief statement of the main points of something."),
            VocabularyWord("vast", "Of very great extent or quantity; immense in size or scope.")
        ),
        "7" to listOf(
            VocabularyWord("approximately", "Close to the actual, but not completely accurate or exact."),
            VocabularyWord("crucial", "Of great importance; decisive or critical to an outcome."),
            VocabularyWord("distinguish", "To recognize or treat someone or something as different."),
            VocabularyWord("emphasis", "Special importance, value, or prominence given to something."),
            VocabularyWord("genuine", "Truly what something is said to be; authentic and real."),
            VocabularyWord("mandatory", "Required by law or rules; compulsory and obligatory."),
            VocabularyWord("perspective", "A particular attitude toward or way of regarding something."),
            VocabularyWord("reluctant", "Unwilling and hesitant; disinclined to do something."),
            VocabularyWord("sufficient", "Enough; adequate for the purpose or to meet a need."),
            VocabularyWord("thorough", "Complete with regard to every detail; not superficial or partial.")
        ),
        "8" to listOf(
            VocabularyWord("break new ground", "To do something innovative that has not been done before."),
            VocabularyWord("come to terms with", "To accept or become reconciled to something difficult."),
            VocabularyWord("draw attention to", "To make people notice or become aware of something."),
            VocabularyWord("give rise to", "To cause something to happen or exist; to bring about."),
            VocabularyWord("in the long run", "Over a long period of time; eventually and ultimately."),
            VocabularyWord("make a distinction", "To recognize or point out a difference between things."),
            VocabularyWord("on the grounds that", "For the reason that; because of a particular justification."),
            VocabularyWord("shed light on", "To help to explain or clarify something previously unclear."),
            VocabularyWord("take into account", "To consider or include something when making a decision."),
            VocabularyWord("with regard to", "Concerning or in connection with a particular subject or matter.")
        ),
        "9" to listOf(
            VocabularyWord("significant", "Sufficiently great or important to be worthy of attention."),
            VocabularyWord("considerable", "Notably large in size, amount, or extent; substantial."),
            VocabularyWord("detrimental", "Tending to cause harm; damaging or injurious to something."),
            VocabularyWord("beneficial", "Resulting in good; favorable, advantageous, or helpful."),
            VocabularyWord("prevalent", "Widespread in a particular area or at a particular time."),
            VocabularyWord("scarce", "Insufficient for the demand; in short supply and hard to find."),
            VocabularyWord("rigid", "Unable to bend or be forced out of shape; strict and inflexible."),
            VocabularyWord("flexible", "Capable of bending easily without breaking; willing to change."),
            VocabularyWord("accelerate", "To begin to move more quickly; to increase in rate or speed."),
            VocabularyWord("impede", "To delay or prevent by obstructing; to hinder progress.")
        ),
        "10" to listOf(
            VocabularyWord("allocate", "To distribute resources or duties for a particular purpose."),
            VocabularyWord("compensate", "To give something, typically money, in recognition of loss or effort."),
            VocabularyWord("deteriorate", "To become progressively worse in condition or quality."),
            VocabularyWord("exacerbate", "To make a problem, bad situation, or negative feeling worse."),
            VocabularyWord("facilitate", "To make an action or process easy or easier to accomplish."),
            VocabularyWord("implement", "To put a decision, plan, or agreement into effect; to carry out."),
            VocabularyWord("mitigate", "To make less severe, serious, or painful; to lessen impact."),
            VocabularyWord("proliferate", "To increase rapidly in number; to multiply and spread."),
            VocabularyWord("sustain", "To strengthen or support physically or mentally over time."),
            VocabularyWord("undermine", "To damage or weaken, especially gradually or insidiously.")
        )
    )

    private val ieltsVocabularySets = listOf(
        VocabularyBook(
            "1", "IELTS Academic Word List",
            "Core academic vocabulary from Coxhead's AWL for the IELTS Academic module",
            570, "Academic"
        ),
        VocabularyBook(
            "2", "IELTS Essential 500",
            "500 most frequently tested words across all IELTS modules",
            500, "General"
        ),
        VocabularyBook(
            "3", "IELTS Band 7+ Advanced",
            "Advanced vocabulary for achieving IELTS band 7 and above",
            300, "Advanced"
        ),
        VocabularyBook(
            "4", "IELTS Speaking Topics",
            "Topic-specific vocabulary for the IELTS Speaking test",
            250, "Speaking"
        ),
        VocabularyBook(
            "5", "IELTS Writing Task 2",
            "Essential vocabulary and phrases for IELTS essay writing",
            200, "Writing"
        ),
        VocabularyBook(
            "6", "IELTS Reading Passages",
            "Key words commonly found in IELTS reading passages",
            400, "Reading"
        ),
        VocabularyBook(
            "7", "IELTS Listening Keywords",
            "Frequently heard words and phrases in the IELTS Listening test",
            350, "Listening"
        ),
        VocabularyBook(
            "8", "IELTS Collocations",
            "Common word combinations essential for a high IELTS score",
            280, "Collocations"
        ),
        VocabularyBook(
            "9", "IELTS Synonyms & Paraphrasing",
            "Key synonyms and paraphrasing techniques for IELTS",
            320, "Vocabulary"
        ),
        VocabularyBook(
            "10", "IELTS Test Booster",
            "Comprehensive vocabulary booster covering all IELTS sections",
            450, "Comprehensive"
        )
    )
}
