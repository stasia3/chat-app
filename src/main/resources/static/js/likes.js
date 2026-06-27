document.addEventListener("DOMContentLoaded", function () {
    console.log("likes.js loaded");

    const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute("content");
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute("content");

    document.querySelectorAll(".like-form").forEach(function (form) {
        form.addEventListener("submit", function (event) {
            event.preventDefault();

            const button = form.querySelector(".like-btn");

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

                    const text = actions.querySelector(".likes-list-btn span:last-child");
                    text.textContent = data.likeCount === 1 ? "like" : "likes";

                    if (data.likedByCurrentUser) {
                        button.classList.add("liked");
                    } else {
                        button.classList.remove("liked");
                    }
                })
                .catch(error => {
                    console.error("Like error:", error);
                });
        });
    });
});