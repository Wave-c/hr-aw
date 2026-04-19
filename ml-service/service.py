from sklearn.metrics.pairwise import cosine_similarity
from model import EmbeddingModel


class EmbeddingService:
    def __init__(self):
        self.model = EmbeddingModel()

    def load_model(self):
        self.model.load()

    def embed(self, text: str):
        return self.model.encode([text])[0]

    def batch_embed(self, texts):
        return self.model.encode(texts)


class SimilarityService:
    def __init__(self, embedding_service: EmbeddingService):
        self.embedding_service = embedding_service

    def rank(self, vacancy: str, candidates: list):
        texts = [c.text for c in candidates]
        ids = [c.id for c in candidates]

        vacancy_vec = self.embedding_service.embed(vacancy)
        candidate_vecs = self.embedding_service.batch_embed(texts)

        scores = cosine_similarity(
            [vacancy_vec],
            candidate_vecs
        )[0]

        results = [
            {"id": ids[i], "score": float(scores[i])}
            for i in range(len(ids))
        ]

        return sorted(results, key=lambda x: x["score"], reverse=True)