package com.example.unseenandstrong.data.local.script

import androidx.sqlite.db.SupportSQLiteDatabase

data class SeedScript(
    val category: String,
    val title: String,
    val gentleText: String,
    val directText: String,
    val firmText: String
)

object ScriptSeedData {
    val scripts: List<SeedScript> = listOf(
        SeedScript(
            category = "Doctor",
            title = "Requesting symptom support",
            gentleText = "I have been managing persistent symptoms, and I would really appreciate your help making a plan that feels manageable for me.",
            directText = "My symptoms are affecting daily function. I need clear next steps for treatment and follow-up.",
            firmText = "These symptoms are significantly impacting my life. I need this concern documented and a concrete care plan today."
        ),
        SeedScript(
            category = "Work",
            title = "Asking for reasonable flexibility",
            gentleText = "I am committed to my role and would appreciate a small adjustment so I can keep contributing consistently.",
            directText = "I need a reasonable accommodation to manage my health while maintaining my work responsibilities.",
            firmText = "I am formally requesting accommodations so I can perform essential duties safely and sustainably."
        ),
        SeedScript(
            category = "Boundary",
            title = "Protecting energy and rest",
            gentleText = "I care about this, but I need to pause and rest right now. I can revisit this when I have capacity.",
            directText = "I cannot take this on today. I need to protect my energy and keep my commitments realistic.",
            firmText = "I am not available for this. Please respect that decision and do not pressure me to explain further."
        ),
        SeedScript(
            category = "Insurance",
            title = "Understanding a denial",
            gentleText = "Could you help me understand why this claim, treatment, medication, or service was denied and what information was used to make that decision?",
            directText = "Please explain the specific reason this claim, treatment, medication, or service was denied and identify the criteria used.",
            firmText = "I need the exact denial reason and the criteria used for this decision before this call ends."
        ),
        SeedScript(
            category = "Insurance",
            title = "Requesting the denial in writing",
            gentleText = "Please send me the denial reason in writing, including any policy language or criteria that applies.",
            directText = "I am requesting a written denial notice that includes the reason, applicable criteria, and next steps available to me.",
            firmText = "Please provide the complete denial decision in writing, including the policy basis and review options."
        ),
        SeedScript(
            category = "Insurance",
            title = "Asking what documentation is missing",
            gentleText = "Could you tell me what information or documentation is still needed so I can work on gathering it?",
            directText = "Please list each missing document or item and explain where it should be submitted.",
            firmText = "I need a complete written list of every missing item and the correct submission instructions."
        ),
        SeedScript(
            category = "Insurance",
            title = "Requesting supervisor review",
            gentleText = "I appreciate your help. I would also like this reviewed by a supervisor or someone with authority to look at the decision again.",
            directText = "Please escalate this matter for supervisor review and document that request in my account.",
            firmText = "I am requesting escalation to a supervisor or review specialist. Please document the escalation and provide a reference number."
        ),
        SeedScript(
            category = "Insurance",
            title = "Starting an appeal",
            gentleText = "Could you walk me through how to begin an appeal and tell me what deadlines or forms I should be aware of?",
            directText = "Please explain the appeal process, required forms, submission address, and applicable deadline.",
            firmText = "I intend to request review of this decision. Provide the appeal instructions, deadline, and required documents in writing."
        ),
        SeedScript(
            category = "Family",
            title = "Explaining an invisible illness",
            gentleText = "I know my symptoms are not always visible, but they are real and they affect what I can do. I need you to trust what I tell you about my body.",
            directText = "My illness is real even when you cannot see it. Please take my limits and symptoms seriously.",
            firmText = "You do not have to see my symptoms for them to be real. I need you to stop questioning whether I am actually ill."
        ),
        SeedScript(
            category = "Family",
            title = "Declining plans because of symptoms",
            gentleText = "I am sorry to miss this. My symptoms and energy are not manageable today, so I need to stay home and rest.",
            directText = "I cannot attend today because of my symptoms. I need to rest and will not be able to change that decision.",
            firmText = "I am not attending. My health requires rest, and I am not available to debate or justify that choice."
        ),
        SeedScript(
            category = "Family",
            title = "Asking relatives not to minimize symptoms",
            gentleText = "I know you may be trying to reassure me, but saying it is not that bad makes me feel dismissed. Please listen without minimizing it.",
            directText = "Please stop minimizing my symptoms. I need support and listening, not comparisons or reassurance that ignores what I am experiencing.",
            firmText = "Comments that minimize my symptoms are not helpful. I need them to stop."
        ),
        SeedScript(
            category = "Family",
            title = "Asking for specific help",
            gentleText = "I could use some help today. Would you be able to handle this one task so I can conserve energy?",
            directText = "I need help with this specific task today. Please tell me whether you can take it on.",
            firmText = "I cannot safely manage this task right now. I need someone else to handle it."
        ),
        SeedScript(
            category = "Family",
            title = "Setting a boundary around advice",
            gentleText = "I know you care about me, but I am not looking for treatment suggestions right now. What would help most is listening.",
            directText = "Please do not give me medical or lifestyle advice unless I ask for it. I need support rather than solutions.",
            firmText = "I am not accepting unsolicited advice about my health. Please stop offering it."
        ),
        SeedScript(
            category = "Strangers",
            title = "Keeping medical information private",
            gentleText = "I prefer to keep my medical information private. Thank you for understanding.",
            directText = "That is private medical information, and I am not discussing it.",
            firmText = "My medical condition is not your business. This conversation is over."
        ),
        SeedScript(
            category = "Strangers",
            title = "Responding to questions about an aid",
            gentleText = "This helps me manage a health condition. I would rather not discuss the details.",
            directText = "I use this because I need it. I am not available to answer personal questions about it.",
            firmText = "Do not question or touch my mobility aid, brace, seat, or other support."
        ),
        SeedScript(
            category = "Strangers",
            title = "Requesting accessible seating or space",
            gentleText = "I need this accessible seat or space because of a health condition. Could you please leave it available for me?",
            directText = "I need access to this seating or space for a disability-related reason. Please make room.",
            firmText = "This accessible space is needed for my health and safety. I need it cleared now."
        ),
        SeedScript(
            category = "Strangers",
            title = "Responding when an accommodation is challenged",
            gentleText = "I understand it may not be obvious, but I need this accommodation. I would appreciate being allowed to use it without questions.",
            directText = "My need is not always visible. I am using an accommodation that I require, and I am not discussing my diagnosis.",
            firmText = "Do not interfere with my accommodation. Direct any concern to staff rather than confronting me."
        ),
        SeedScript(
            category = "Strangers",
            title = "Ending an intrusive conversation",
            gentleText = "I am not comfortable continuing this conversation, so I am going to step away now.",
            directText = "I have answered as much as I choose to. I am ending this conversation.",
            firmText = "Stop asking me personal questions. Do not follow me or continue this conversation."
        )
    )

    fun insertMissing(db: SupportSQLiteDatabase) {
        scripts.forEach { script ->
            db.execSQL(
                """
                INSERT INTO scripts (category, title, gentleText, directText, firmText)
                SELECT ?, ?, ?, ?, ?
                WHERE NOT EXISTS (
                    SELECT 1 FROM scripts WHERE category = ? AND title = ?
                )
                """.trimIndent(),
                arrayOf<Any>(
                    script.category,
                    script.title,
                    script.gentleText,
                    script.directText,
                    script.firmText,
                    script.category,
                    script.title
                )
            )
        }
    }
}
