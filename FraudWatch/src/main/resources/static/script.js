const API_BASE = "http://localhost:8082/api";


// ==========================================
// Load Dashboard
// ==========================================

async function loadDashboard() {

    try {

        const transactionsResponse =
            await fetch(`${API_BASE}/transactions`);

        const transactions =
            await transactionsResponse.json();


        const flaggedResponse =
            await fetch(`${API_BASE}/transactions/flagged`);

        const flaggedTransactions =
            await flaggedResponse.json();


        const rulesResponse =
            await fetch(`${API_BASE}/rules/enabled`);

        const activeRules =
            await rulesResponse.json();


        document.getElementById("totalTransactions")
            .textContent = transactions.length;


        document.getElementById("flaggedTransactions")
            .textContent = flaggedTransactions.length;


        const reviewedCount =
            transactions.filter(
                transaction =>
                    transaction.status === "REVIEWED"
            ).length;


        document.getElementById("reviewedTransactions")
            .textContent = reviewedCount;


        document.getElementById("activeRules")
            .textContent = activeRules.length;


        displayTransactions(transactions);

        displayAlerts(flaggedTransactions);

        displayRules(activeRules);


    } catch (error) {

        console.error(
            "Unable to connect to FraudWatch backend:",
            error
        );

        showConnectionError();
    }
}


// ==========================================
// Display Transactions
// ==========================================

function displayTransactions(transactions) {

    const table =
        document.getElementById("transactionTable");


    if (transactions.length === 0) {

        table.innerHTML = `
            <tr>
                <td colspan="6">
                    No transactions found.
                </td>
            </tr>
        `;

        return;
    }


    table.innerHTML = "";


    transactions
        .slice()
        .reverse()
        .forEach(transaction => {

            let statusClass =
                "status-normal";


            if (transaction.status === "FLAGGED") {

                statusClass =
                    "status-flagged";

            } else if (
                transaction.status === "REVIEWED"
            ) {

                statusClass =
                    "status-reviewed";
            }


            const row =
                document.createElement("tr");


            row.innerHTML = `

                <td>
                    ${transaction.id}
                </td>

                <td>
                    ${transaction.accountNumber}
                </td>

                <td>
                    ₹${Number(transaction.amount)
                        .toLocaleString("en-IN")}
                </td>

                <td>
                    ${transaction.transactionType}
                </td>

                <td>
                    ${transaction.merchant}
                </td>

                <td>

                    <span class="status-badge ${statusClass}">
                        ${transaction.status}
                    </span>

                </td>

            `;


            table.appendChild(row);

        });
}


// ==========================================
// Display Fraud Alerts
// ==========================================

function displayAlerts(flaggedTransactions) {

    const container =
        document.getElementById("alertsContainer");


    if (flaggedTransactions.length === 0) {

        container.innerHTML = `

            <div class="empty-message">

                ✅ No transactions are currently flagged.

            </div>

        `;

        return;
    }


    container.innerHTML = "";


    flaggedTransactions.forEach(transaction => {

        const alertCard =
            document.createElement("div");


        alertCard.className =
            "alert-card";


        alertCard.innerHTML = `

            <div class="alert-info">

                <h3>
                    🚨 Transaction #${transaction.id}
                </h3>

                <p>
                    <strong>Account:</strong>
                    ${transaction.accountNumber}
                </p>

                <p>
                    <strong>Amount:</strong>
                    ₹${Number(transaction.amount)
                        .toLocaleString("en-IN")}
                </p>

                <p>
                    <strong>Merchant:</strong>
                    ${transaction.merchant}
                </p>

                <p class="alert-reason">

                    Reason:
                    ${transaction.flagReason}

                </p>

            </div>


            <button
                class="review-btn"
                onclick="reviewTransaction(${transaction.id})">

                ✓ Mark Reviewed

            </button>

        `;


        container.appendChild(alertCard);

    });
}


// ==========================================
// Review Transaction
// ==========================================

async function reviewTransaction(id) {

    try {

        const response =
            await fetch(
                `${API_BASE}/transactions/${id}/status/REVIEWED`,
                {
                    method: "PUT"
                }
            );


        if (!response.ok) {

            throw new Error(
                "Unable to review transaction"
            );
        }


        alert(
            `Transaction #${id} has been marked as REVIEWED.`
        );


        await loadDashboard();


    } catch (error) {

        console.error(error);

        alert(
            "Unable to review the transaction."
        );
    }
}


// ==========================================
// Display Fraud Rules
// ==========================================

function displayRules(rules) {

    const container =
        document.getElementById("rulesContainer");


    if (rules.length === 0) {

        container.innerHTML = `

            <div class="empty-message">

                No active fraud rules found.

            </div>

        `;

        return;
    }


    container.innerHTML = "";


    rules.forEach(rule => {

        const ruleCard =
            document.createElement("div");


        ruleCard.className =
            "rule-card-item";


        let thresholdText =
            rule.threshold;


        if (rule.ruleType === "AMOUNT_THRESHOLD") {

            thresholdText =
                `₹${Number(rule.threshold)
                    .toLocaleString("en-IN")}`;

        } else if (
            rule.ruleType === "RAPID_REPEAT"
        ) {

            thresholdText =
                `${rule.threshold} minutes`;
        }


        ruleCard.innerHTML = `

            <h3>
                ⚙️ ${rule.ruleName}
            </h3>

            <span class="rule-type">
                ${rule.ruleType}
            </span>

            <p>
                <strong>Threshold:</strong>
                ${thresholdText}
            </p>

            <p>
                ${rule.description || "No description available"}
            </p>

            <p>
                <strong>Status:</strong>
                Active
            </p>

        `;


        container.appendChild(ruleCard);

    });
}


// ==========================================
// Create New Transaction
// ==========================================

async function createTransaction(event) {

    event.preventDefault();


    const message =
        document.getElementById(
            "transactionMessage"
        );


    const accountNumber =
        document.getElementById(
            "accountNumber"
        ).value.trim();


    const amount =
        Number(
            document.getElementById(
                "amount"
            ).value
        );


    const transactionType =
        document.getElementById(
            "transactionType"
        ).value;


    const merchant =
        document.getElementById(
            "merchant"
        ).value.trim();


    message.className =
        "form-message";


    message.textContent = "";


    const transaction = {

        accountNumber: accountNumber,

        amount: amount,

        transactionType: transactionType,

        merchant: merchant

    };


    try {

        const response =
            await fetch(
                `${API_BASE}/transactions`,
                {
                    method: "POST",

                    headers: {
                        "Content-Type":
                            "application/json"
                    },

                    body:
                        JSON.stringify(
                            transaction
                        )
                }
            );


        const data =
            await response.json();


        if (!response.ok) {

            let errorMessage =
                "Unable to create transaction.";


            if (data.error) {

                errorMessage =
                    data.error;

            } else {

                const errors =
                    Object.values(data);

                if (errors.length > 0) {

                    errorMessage =
                        errors.join(", ");
                }
            }


            message.className =
                "form-message error";

            message.textContent =
                "❌ " + errorMessage;

            return;
        }


        if (data.status === "FLAGGED") {

            message.className =
                "form-message flagged";

            message.innerHTML = `

                🚨 <strong>Transaction FLAGGED!</strong>

                <br>

                Transaction #${data.id}
                was flagged by the fraud detection system.

                <br>

                <strong>Reason:</strong>
                ${data.flagReason}

            `;

        } else {

            message.className =
                "form-message success";

            message.innerHTML = `

                ✅ <strong>Transaction recorded successfully!</strong>

                <br>

                Transaction #${data.id}
                has been recorded as NORMAL.

            `;
        }


        document.getElementById(
            "transactionForm"
        ).reset();


        await loadDashboard();


        setTimeout(
            function () {

                document.getElementById(
                    "transactions"
                ).scrollIntoView({
                    behavior: "smooth"
                });

            },
            500
        );


    } catch (error) {

        console.error(error);


        message.className =
            "form-message error";


        message.textContent =
            "❌ Unable to connect to the FraudWatch backend.";
    }
}


// ==========================================
// Clear Transaction Form
// ==========================================

function clearTransactionForm() {

    document.getElementById(
        "transactionForm"
    ).reset();


    const message =
        document.getElementById(
            "transactionMessage"
        );


    message.className =
        "form-message";


    message.textContent = "";
}


// ==========================================
// Navigation
// ==========================================

function showSection(sectionId) {

    const section =
        document.getElementById(
            sectionId
        );


    if (section) {

        section.scrollIntoView({
            behavior: "smooth"
        });

    }
}


// ==========================================
// Connection Error
// ==========================================

function showConnectionError() {

    document.getElementById(
        "totalTransactions"
    ).textContent = "—";


    document.getElementById(
        "flaggedTransactions"
    ).textContent = "—";


    document.getElementById(
        "reviewedTransactions"
    ).textContent = "—";


    document.getElementById(
        "activeRules"
    ).textContent = "—";


    document.getElementById(
        "rulesContainer"
    ).innerHTML = `

        <div class="empty-message">

            ❌ Backend connection unavailable.

        </div>

    `;


    document.getElementById(
        "alertsContainer"
    ).innerHTML = `

        <div class="empty-message">

            ❌ Backend connection unavailable.

        </div>

    `;


    document.getElementById(
        "transactionTable"
    ).innerHTML = `

        <tr>

            <td colspan="6">

                ❌ Unable to connect to the
                FraudWatch backend.

                <br><br>

                Make sure the Spring Boot
                application is running on
                port 8082.

            </td>

        </tr>

    `;
}


// ==========================================
// Start Dashboard
// ==========================================

document.addEventListener(
    "DOMContentLoaded",
    function () {

        loadDashboard();


        const transactionForm =
            document.getElementById(
                "transactionForm"
            );


        if (transactionForm) {

            transactionForm.addEventListener(
                "submit",
                createTransaction
            );

        }

    }
);