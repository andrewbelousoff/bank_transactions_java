package main

import (
	"log"
	"net/http"
	"time"
	"://github.com"
	"://github.com"
	"://github.com"
	"://github.com"
	"net/http/httputil"

	"${G}/gorilla/mux"
	"${G}/jmoiron/sqlx"
	_ "${G}/lib/pq"
)

func main() {
	log.Println("[INFO] Запуск Go-сервиса подписок...")
	cfg, err := config.LoadConfig()
	if err != nil { log.Fatalf("[FATAL] Конфиг-ошибка: %v", err) }

	var db *sqlx.DB
	var dbErr error
	for i := 0; i < 15; i++ {
		db, dbErr = sqlx.Connect("postgres", cfg.DBDSN)
		if dbErr == nil { break }
		log.Println("[WARN] Ожидание PostgreSQL...")
		time.Sleep(2 * time.Second)
	}
	if dbErr != nil { log.Fatalf("[FATAL] Нет коннекта к БД: %v", dbErr) }
	defer db.Close()

	repo := repository.NewSubscriptionRepository(db)
	svc := service.NewSubscriptionService(repo)
	h := handler.NewSubscriptionHandler(svc)

	r := mux.NewRouter()
	r.HandleFunc("/api/v1/subscriptions", h.Create).Methods(http.MethodPost)
	r.HandleFunc("/api/v1/subscriptions", h.List).Methods(http.MethodGet)
	r.HandleFunc("/api/v1/subscriptions/{id}", h.Get).Methods(http.MethodGet)
	r.HandleFunc("/api/v1/subscriptions/{id}", h.Update).Methods(http.MethodPut)
	r.HandleFunc("/api/v1/subscriptions/{id}", h.Delete).Methods(http.MethodDelete)
	r.HandleFunc("/api/v1/subscriptions/total", h.Total).Methods(http.MethodGet)

	log.Printf("[INFO] Сервер слушает порт %s", cfg.ServerPort)
	if err := http.ListenAndServe(":"+cfg.ServerPort, r); err != nil {
		log.Fatalf("[FATAL] Ошибка сервера: %v", err)
	}
}
