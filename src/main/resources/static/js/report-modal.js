document.addEventListener("DOMContentLoaded", function () {
    const modal = document.getElementById("reportModal");
    const form = document.getElementById("reportForm");
    const postIdInput = document.getElementById("reportPostId");
    const reasonInput = document.getElementById("reportReason");
    const detailsInput = document.getElementById("reportDetails");

    const closeBtn = document.getElementById("closeReportModal");
    const cancelBtn = document.getElementById("cancelReportModal");

    function openModal(button) {
        const reportUrl = button.dataset.reportUrl;
        const postId = button.dataset.postId || "";

        form.action = reportUrl;
        postIdInput.value = postId;

        reasonInput.value = "";
        detailsInput.value = "";

        modal.classList.remove("hidden");
    }

    function closeModal() {
        modal.classList.add("hidden");
    }

    document.querySelectorAll(".report-trigger").forEach(button => {
        button.addEventListener("click", function () {
            openModal(button);
        });
    });

    closeBtn.addEventListener("click", closeModal);
    cancelBtn.addEventListener("click", closeModal);

    modal.addEventListener("click", function (event) {
        if (event.target === modal) {
            closeModal();
        }
    });
});