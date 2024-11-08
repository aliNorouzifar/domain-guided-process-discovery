# IMr + LLM
### General Information
This repository is associated with the paper accepted for presentation at the AI4BPM Workshop at the BPM 2024 Conference. This is an initial version, with planned updates and improvements to follow.

```bibtex
@article{DBLP:journals/corr/abs-2408-17316,
  author       = {Ali Norouzifar and
                  Humam Kourani and
                  Marcus Dees and
                  Wil M. P. van der Aalst},
  title        = {Bridging Domain Knowledge and Process Discovery Using Large Language
                  Models},
  journal      = {CoRR},
  volume       = {abs/2408.17316},
  year         = {2024},
  url          = {https://doi.org/10.48550/arXiv.2408.17316},
  doi          = {10.48550/ARXIV.2408.17316},
  eprinttype    = {arXiv},
  eprint       = {2408.17316},
  timestamp    = {Sat, 28 Sep 2024 20:40:16 +0200},
  biburl       = {https://dblp.org/rec/journals/corr/abs-2408-17316.bib},
  bibsource    = {dblp computer science bibliography, https://dblp.org}}
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

3. **Rules File**:
   - Provide a text file containing declarative constraints, as outlined in the referenced paper.
   - Currently, our setup does not support API calls, so you will need to manually run prompts with your LLM tool and paste the generated rules into a text file.
   - **Format**: Each line in the file should contain one rule, chosen from the following set of declarative constraints:
     - `{existence, responded_existence, response, precedence, coexistence, noncoexistence, nonsuccession, atmost1}`
   - See the "motivating example" files for a template and examples. Another example from the paper:
```text
not-co-existence(Block Claim 2, Block Claim 1)
not-co-existence(Block Claim 2, Block Claim 3)
co-existence(Block Claim 1, Unblock Claim 1)
co-existence(Block Claim 2, Unblock Claim 2)
co-existence(Block Claim 3, Unblock Claim 3)
precedence(Block Claim 1, Unblock Claim 1)
precedence(Block Claim 2, Unblock Claim 2)
precedence(Block Claim 3, Unblock Claim 3)
not-co-existence(Receive Objection 1, Receive Objection 2)
precedence(Reject Claim, Receive Objection 2)
precedence(Payment Order, Receive Objection 1)
at-most(Correct Claim)
precedence(Block Claim 1, Correct Claim)
precedence(Correct Claim, Unblock Claim 1)
response(Withdraw Claim, Repayment)
responded-existence(Accept Claim, Payment Order)
responded-existence(Payment Order, Execute Payment)
```