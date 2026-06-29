package com.research.insight_service.util;

import org.springframework.stereotype.Component;

@Component
public class PromptBuilder {

    public String buildComparisonPrompt(String analyses) {

        return """
                You are an expert research scientist.

                Compare the following research papers.

                Generate the response ONLY in GitHub Markdown.

                Follow EXACTLY this structure.

                # Summary

                Write a concise summary comparing all papers.

                ## Comparison Table

                Create a markdown table with columns for every paper.

                Rows should include:

                - Research Domain
                - Problem Statement
                - Methodology
                - Dataset
                - Novelty Score
                - Research Gap
                - Future Work
                - Difficulty

                ## Strengths

                Mention strengths of each paper.

                ## Weaknesses

                Mention weaknesses of each paper.

                ## Recommendation

                Recommend the strongest paper and explain why.

                Here are the analyses:

                %s
                """.formatted(analyses);

    }

    public String buildInsightPrompt(String analysis) {

        return """
                You are an expert research scientist.

                A research paper has already been analyzed.

                Your task is to generate a professional research report.

                Generate ONLY GitHub Markdown.

                Follow EXACTLY this structure.

                # Executive Summary

                Provide a concise explanation of the paper.

                # Research Overview

                Explain

                - Research Domain
                - Main Problem
                - Motivation

                # Methodology

                Explain the proposed methodology in simple language.

                # Key Contributions

                Mention the major contributions.

                # Keywords

                Provide bullet points.

                # Novelty Analysis

                Explain how innovative the work is.

                # Research Gap

                Explain what gap this paper attempts to solve.

                # Future Scope

                Suggest future improvements.

                # Limitations

                Mention possible limitations.

                # Difficulty Level

                Explain whether this paper is suitable for

                - Beginner
                - Intermediate
                - Advanced

                # Practical Applications

                Mention industries and real-world use cases.

                # Final Verdict

                Provide a concise evaluation of the paper.

                Here is the existing analysis.

                %s
                """.formatted(analysis);

    }

    public String buildResearchGapPrompt(String analyses) {

        return """
                You are a senior research scientist and reviewer for top conferences like NeurIPS, ICML, ACL, CVPR, IEEE, Springer, ACM, and Elsevier.

                You are given AI analyses of multiple research papers.

                Your task is to analyze ALL the papers together and identify genuine research gaps.

                Do NOT compare the papers individually.
                Instead, think collectively and identify:

                - What problems are already solved
                - What limitations exist across all papers
                - What challenges remain unsolved
                - What promising research opportunities exist
                - What novel ideas can be explored
                - What Master's thesis topics can be derived
                - What PhD-level research directions can emerge
                - What ideas have high publication potential

                Return ONLY GitHub Markdown.

                Use exactly the following structure.

                # Research Gap Discovery

                ## Research Domain

                Mention the common research domain.

                ---

                ## Common Problems Addressed

                Summarize what all papers collectively attempt to solve.

                ---

                ## Existing Approaches

                Explain how current papers solve these problems.

                ---

                ## Common Limitations

                Mention recurring limitations across all papers.

                ---

                ## Unsolved Problems

                Identify the biggest unanswered problems.

                ---

                ## Major Research Gaps

                Clearly list the research gaps.

                ---

                ## Future Research Opportunities

                Suggest innovative directions.

                ---

                ## Suggested Master's Thesis Topics

                Provide 5 concrete thesis ideas.

                ---

                ## Suggested PhD Research Directions

                Provide 5 advanced research ideas.

                ---

                ## Publication Opportunities

                Suggest ideas suitable for publication in top conferences or journals.

                ---

                ## Final Recommendation

                Explain which research direction is the most impactful.

                Below are the AI analyses of the selected papers.

                %s
                """
                .formatted(analyses);

    }

    public String buildLiteratureReviewPrompt(String analyses) {

        return """
                You are a senior researcher writing a survey paper for top conferences like IEEE, ACM, Springer, Elsevier, NeurIPS, ICML, ACL and CVPR.

                You are given AI analyses of multiple research papers.

                Your task is to write a COMPLETE literature review by synthesizing all papers.

                Do NOT summarize each paper individually.

                Instead:

                - Identify common themes.
                - Organize papers by ideas and approaches.
                - Explain how the field evolved.
                - Discuss similarities and differences.
                - Mention strengths and weaknesses.
                - Identify research gaps.
                - Suggest future directions.

                Return ONLY GitHub Markdown.

                Use exactly the following structure.

                # Literature Review

                ## Abstract

                Write a concise abstract summarizing the literature review.

                ---

                ## Introduction

                Introduce the research domain and explain why this topic is important.

                ---

                ## Research Background

                Provide background of the field.

                ---

                ## Existing Research

                Discuss existing work by combining ideas from all papers instead of describing papers one by one.

                ---

                ## Comparative Discussion

                Explain:

                - similarities
                - differences
                - advantages
                - disadvantages

                of existing approaches.

                ---

                ## Current Trends

                Describe recent trends observed across the papers.

                ---

                ## Research Gaps

                Identify major gaps that still exist.

                ---

                ## Future Research Directions

                Suggest promising future work.

                ---

                ## Conclusion

                Summarize the overall state of research.

                ---

                ## References

                Create references using this format.

                - Paper ID 43
                - Paper ID 44
                - Paper ID ...

                Below are the analyses of the selected papers.

                %s
                """
                .formatted(analyses);

    }
}