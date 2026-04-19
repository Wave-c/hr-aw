from pydantic import BaseModel
from typing import List


class TextRequest(BaseModel):
    text: str


class BatchTextRequest(BaseModel):
    texts: List[str]


class CandidateItem(BaseModel):
    id: str
    text: str


class ScoreRequest(BaseModel):
    vacancy: str
    candidates: List[CandidateItem]


class ScoreResponseItem(BaseModel):
    id: str
    score: float