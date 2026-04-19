from fastapi import FastAPI
from service import EmbeddingService, SimilarityService
from schemas import TextRequest, BatchTextRequest, ScoreRequest

app = FastAPI()

embedding_service = EmbeddingService()
similarity_service = SimilarityService(embedding_service)


@app.on_event("startup")
def startup():
    embedding_service.load_model()


@app.post("/embed")
def embed(req: TextRequest):
    vec = embedding_service.embed(req.text)
    return {"vector": vec.tolist()}


@app.post("/batch-embed")
def batch_embed(req: BatchTextRequest):
    vecs = embedding_service.batch_embed(req.texts)
    return {"vectors": [v.tolist() for v in vecs]}


@app.post("/score")
def score(req: ScoreRequest):
    return similarity_service.rank(req.vacancy, req.candidates)