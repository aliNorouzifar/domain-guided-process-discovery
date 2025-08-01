# IMr + LLM
### General Information
This repository is associated with the paper accepted for presentation at the AI4BPM Workshop at the BPM 2024 Conference. This code is related to the extended version of this paper that is currently under review.

```bibtex
@inproceedings{DBLP:conf/bpm/NorouzifarKDA24,
  author       = {Ali Norouzifar and
                  Humam Kourani and
                  Marcus Dees and
                  Wil M. P. van der Aalst},
  editor       = {Katarzyna Gdowska and
                  Mar{\'{\i}}a Teresa G{\'{o}}mez{-}L{\'{o}}pez and
                  Jana{-}Rebecca Rehse},
  title        = {Bridging Domain Knowledge and Process Discovery Using Large Language
                  Models},
  booktitle    = {Business Process Management Workshops - {BPM} 2024 International Workshops,
                  Krakow, Poland, September 1-6, 2024, Revised Selected Papers},
  series       = {Lecture Notes in Business Information Processing},
  volume       = {534},
  pages        = {44--56},
  publisher    = {Springer},
  year         = {2024},
  url          = {https://doi.org/10.1007/978-3-031-78666-2\_4},
  doi          = {10.1007/978-3-031-78666-2\_4},
  timestamp    = {Fri, 07 Mar 2025 18:29:52 +0100},
  biburl       = {https://dblp.org/rec/conf/bpm/NorouzifarKDA24.bib},
  bibsource    = {dblp computer science bibliography, https://dblp.org},
}
```

### Installation

To get started, install the required packages by running:

```bash
pip install -r requirements.txt
```

### Usage

Upon running the code, a simple GUI will open, prompting you to enter three inputs:

1. **Support Parameter**:
   - Set a value ideally between **0.2 and 0.4** if you're unsure about optimal values.
   - For further details on this parameter, refer to the recommended research papers listed below.
    
```bibtex
   @inproceedings{DBLP:conf/rcis/NorouzifarDA24,
  author       = {Ali Norouzifar and
                  Marcus Dees and
                  Wil M. P. van der Aalst},
  editor       = {Jo{\~{a}}o Ara{\'{u}}jo and
                  Jose Luis de la Vara and
                  Maribel Yasmina Santos and
                  Sa{\"{\i}}d Assar},
  title        = {Imposing Rules in Process Discovery: An Inductive Mining Approach},
  booktitle    = {Research Challenges in Information Science - 18th International Conference,
                  {RCIS} 2024, Guimar{\~{a}}es, Portugal, May 14-17, 2024, Proceedings,
                  Part {I}},
  series       = {Lecture Notes in Business Information Processing},
  volume       = {513},
  pages        = {220--236},
  publisher    = {Springer},
  year         = {2024},
  url          = {https://doi.org/10.1007/978-3-031-59465-6\_14},
  doi          = {10.1007/978-3-031-59465-6\_14},
  timestamp    = {Fri, 17 May 2024 21:42:03 +0200},
  biburl       = {https://dblp.org/rec/conf/rcis/NorouzifarDA24.bib},
  bibsource    = {dblp computer science bibliography, https://dblp.org}}
```
   and 

    ```bibtex
    @inproceedings{DBLP:conf/sac/NorouzifarA23,
      author       = {Ali Norouzifar and
                      Wil M. P. van der Aalst},
      editor       = {Jiman Hong and
                      Maart Lanperne and
                      Juw Won Park and
                      Tom{\'{a}}s Cern{\'{y}} and
                      Hossain Shahriar},
      title        = {Discovering Process Models that Support Desired Behavior and Avoid
                      Undesired Behavior},
      booktitle    = {Proceedings of the 38th {ACM/SIGAPP} Symposium on Applied Computing,
                      {SAC} 2023, Tallinn, Estonia, March 27-31, 2023},
      pages        = {365--368},
      publisher    = {{ACM}},
      year         = {2023},
      url          = {https://doi.org/10.1145/3555776.3577818},
      doi          = {10.1145/3555776.3577818},
      timestamp    = {Fri, 21 Jul 2023 22:25:35 +0200},
      biburl       = {https://dblp.org/rec/conf/sac/NorouzifarA23.bib},
      bibsource    = {dblp computer science bibliography, https://dblp.org}
    }
    ```


2. **Event Log**:
   - Upload an event log file in **XES format**.

```