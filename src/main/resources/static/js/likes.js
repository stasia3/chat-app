document.addEventListener("DOMContentLoaded", function () {
    console.log("likes.js loaded");

    const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute("content");
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute("content");

    document.querySelectorAll(".like-form").forEach(function (form) {
        form.addEventListener("submit", function (event) {
            event.preventDefault();

            const button = form.querySelector(".like-btn");
            const icon = button.querySelector(".like-icon");

            fetch(form.action, {
                method: "POST",
                headers: {
                    "X-Requested-With": "XMLHttpRequest",
                    [csrfHeader]: csrfToken
                }
            })
                .then(response => {
                    if (!response.ok) {
                        throw new Error("Request failed: " + response.status);
                    }
                    return response.json();
                })
                .then(data => {
                    const actions = form.closest(".post-card-actions");
                    const count = actions.querySelector(".likes-list-btn .like-count");

                    count.textContent = data.likeCount;

                    if (data.likedByCurrentUser) {
                        button.classList.add("liked");
                        icon.classList.remove("fa-regular");
                        icon.classList.add("fa-solid");
                    } else {
                        button.classList.remove("liked");
                        icon.classList.remove("fa-solid");
                        icon.classList.add("fa-regular");
                    }
                })
                .catch(error => {
                    console.error("Like error:", error);
                });
        });
    });
});